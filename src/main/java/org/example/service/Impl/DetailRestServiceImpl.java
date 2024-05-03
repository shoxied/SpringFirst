package org.example.service.Impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.cache.RabbitListener;
import org.example.converter.DetailConverter;
import org.example.dao.DetailRepository;
import org.example.dao.ValueRepository;
import org.example.dao.ext.DetailResultExt;
import org.example.dao.ext.DetailResultExtValues;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.AttributeValue;
import org.example.entity.Detail;
import org.example.entity.ext.DetailList;
import org.example.search.dto.SearchDetailDto;
import org.example.search.dto.SearchDetailValueDto;
import org.example.search.repo.SearchDetailRepo;
import org.example.service.DetailRestService;
import org.example.service.PersistenceService;
import org.example.service.SearchService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class DetailRestServiceImpl implements DetailRestService {

    private final DetailRepository detailRepository;
    private final ValueRepository valueRepository;
    private final SearchDetailRepo searchDetailRepo;

    private final RabbitTemplate rabbitTemplate;

    private final PersistenceService persistenceService;
    private final SearchService searchService;

    private final DetailConverter detailConverter;

    @Override
    public DetailResultExt getDetails(String name, String brand, Integer valueId, Integer page) {

        SearchHits<SearchDetailDto> searchHits = searchService.getDetails(name, brand, valueId, page);

        if(page == null){
            page = 1;
        }

        ElasticsearchAggregations aggs = (ElasticsearchAggregations)searchHits.getAggregations();
        ElasticsearchAggregation aggsBrand = aggs.get("brands");

        StringTermsAggregate brandTerms = aggsBrand.aggregation().getAggregate().sterms();

        Buckets<StringTermsBucket> brandBuckets = brandTerms.buckets();

        ElasticsearchAggregation aggsAttr = aggs.get("attributes");
        NestedAggregate attrTerms = aggsAttr.aggregation().getAggregate().nested();
        LongTermsAggregate lAttrTerms = attrTerms.aggregations().get("value_ids").lterms();
        Buckets<LongTermsBucket> attrBuckets = lAttrTerms.buckets();

        int totalPages = (int)Math.ceil((double)searchHits.getTotalHits() / 4.0);
        return detailConverter.dto2DetailResultExt(
                searchHits.getSearchHits().stream().map(SearchHit::getContent).toList(),
                brandBuckets,
                attrBuckets,
                searchHits.getTotalHits(),
                totalPages,
                page);
    }

    @Override
    public Detail addDetail(DetailUpdate update) {

        Detail detail = persistenceService.saveDetail(update);
        rabbitTemplate.convertAndSend(RabbitListener.EXCHANGE,
                RabbitListener.ROUTING_KEY,
                detail.getId());
        return detail;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Detail> addDetails(DetailList update) {
        List<Detail> details = new ArrayList<>();
        int count = 0;

        for (DetailUpdate detailUpdate:update.getDetails()) {
            List<AttributeValue> values = new ArrayList<>();

            if (detailUpdate.getValues() != null) {
                for (Integer valueId : detailUpdate.getValues()) {
                    values.add(AttributeValue.builder()
                            .value(valueRepository.findById(valueId).get())
                            .build());
                }
            }

            Detail detail = Detail.builder()
                    .brand(detailUpdate.getBrand())
                    .oem(detailUpdate.getOem())
                    .name(detailUpdate.getName())
                    .attributeValues(values)
                    .build();

            if(detailRepository.findByBrand(detail.getBrand()).size() != 0 && detailRepository.findByOem(detail.getOem()).size() != 0){
                count++;
                log.warn("not unique detail identifier, {} objects didn't saved", count);
            }
            else{
                details.add(detail);
            }
        }

        List<Detail> savedList = StreamSupport.stream(detailRepository.saveAll(details).spliterator(), false).toList();
        log.info("save {} objects to db", details.size());

        List<SearchDetailDto> detailDtos = new ArrayList<>();
        for(Detail detail:savedList){

            SearchDetailDto searchDetailDto = new SearchDetailDto();
            searchDetailDto.setId(detail.getId());
            searchDetailDto.setBrand(detail.getBrand());
            searchDetailDto.setOem(detail.getOem());
            searchDetailDto.setName(detail.getName());

            List<SearchDetailValueDto> attributes = new ArrayList<>();
            for (AttributeValue attributeValue:detail.getAttributeValues()){
                SearchDetailValueDto searchDetailValueDto = new SearchDetailValueDto();
                searchDetailValueDto.setId(attributeValue.getId());
                searchDetailValueDto.setAttributeId(attributeValue.getValue().getAttribute().getId());
                searchDetailValueDto.setAttributeName(attributeValue.getValue().getAttribute().getName());
                searchDetailValueDto.setValueId(attributeValue.getValue().getId());
                searchDetailValueDto.setValue(attributeValue.getValue().getValue());
                attributes.add(searchDetailValueDto);
            }

            searchDetailDto.setAttributes(attributes);
            detailDtos.add(searchDetailDto);
        }

        log.info("save {} objects to elastic", detailDtos.size());
        searchDetailRepo.saveAll(detailDtos);
        return savedList;
    }
}
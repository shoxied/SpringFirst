package org.example.service.Impl;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.converter.DetailConverter;
import org.example.dao.DetailRepository;
import org.example.dao.ValueRepository;
import org.example.dao.ext.DetailExt;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.AttributeValue;
import org.example.entity.Detail;
import org.example.entity.ext.DetailList;
import org.example.search.dto.SearchDetailDto;
import org.example.search.dto.SearchDetailValueDto;
import org.example.search.repo.SearchDetailRepo;
import org.example.service.DetailRestService;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class DetailRestServiceImpl implements DetailRestService {

    private final DetailRepository detailRepository;
    private final ValueRepository valueRepository;
    private final SearchDetailRepo searchDetailRepo;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<DetailExt> getDetails(String name) {
        DetailConverter detailConverter = new DetailConverter();
        NativeQueryBuilder builder = new NativeQueryBuilder();
        SearchHits<SearchDetailDto> searchHits;
        NativeQuery nativeQuery;
        Aggregation aggregation = AggregationBuilders.terms(ta->ta.field("string_value"));

        if (name != null){
            nativeQuery = builder.withQuery(q->q.match(m->m.field("name").query(name)))
                    .withAggregation("attributes", aggregation).build();
        }
        else {
            nativeQuery = builder.withQuery(q->q.bool(b->b)).withAggregation("attributes", aggregation).build();
        }

        searchHits = elasticsearchOperations.search(nativeQuery, SearchDetailDto.class);
        return detailConverter.dtoToDetailExt(searchHits.getSearchHits().stream().map(SearchHit::getContent).toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Detail addDetail(DetailUpdate update) {

        List<AttributeValue> values = new ArrayList<>();

        if (update.getValues() != null){
            for (Integer valueId: update.getValues()){
                values.add(AttributeValue.builder()
                        .value(valueRepository.findById(valueId).get())
                        .build());
            }
        }

        Detail savedDetail = Detail.builder()
                .brand(update.getBrand())
                .oem(update.getOem())
                .name(update.getName())
                .attributeValues(values)
                .build();
        Detail detail = detailRepository.save(savedDetail);

        List<SearchDetailValueDto> attributes = new ArrayList<>();

        if (update.getValues() != null){
            for (AttributeValue attribute: detail.getAttributeValues()){
                attributes.add(SearchDetailValueDto.builder()
                        .id(attribute.getId())
                        .attributeId(attribute.getValue().getAttribute().getId())
                        .attributeName(attribute.getValue().getAttribute().getName())
                        .valueId(attribute.getValue().getId())
                        .value(attribute.getValue().getValue())
                        .build());

            }
        }

        searchDetailRepo.save(SearchDetailDto.builder()
                .id(detail.getId())
                .brand(detail.getBrand())
                .oem(detail.getOem())
                .name(detail.getName())
                .attributes(attributes)
                .build());

        return detail;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Detail> addDetails(DetailList update) {
        List<Detail> details = new ArrayList<>();

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
            
            details.add(detail);
        }

        log.info("save {} objects to db", details.size());
        for(Detail detail:details){
            if(detailRepository.findByBrand(detail.getBrand()).size() != 0 && detailRepository.findByOem(detail.getOem()).size() != 0){
                log.warn("not unique detail identifier");
                return null;
            }
        }
        List<Detail> savedList = StreamSupport.stream(detailRepository.saveAll(details).spliterator(), false).toList();

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
        return details;
    }
}
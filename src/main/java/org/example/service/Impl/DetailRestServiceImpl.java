package org.example.service.Impl;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import lombok.RequiredArgsConstructor;
import org.example.converter.DetailConverter;
import org.example.dao.DetailRepository;
import org.example.dao.ValueRepository;
import org.example.dao.ext.DetailExt;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.AttributeValue;
import org.example.entity.Detail;
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
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
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
}

package org.example.service.Impl;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.dao.ext.DetailExt;
import org.example.search.dto.SearchDetailDto;
import org.example.service.SearchService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    @Override
    public SearchHits<SearchDetailDto> getDetails(String name, String brand, Integer valueId, Integer page) {

        NativeQueryBuilder builder = new NativeQueryBuilder();
        NativeQuery nativeQuery;
        Aggregation aggregationBrand = AggregationBuilders.terms(ta->ta.field("brand"));

        Aggregation sub = new Aggregation.Builder().terms(ta -> ta.field("attributes.value_id")).build();
        Aggregation aggregationAttr = new Aggregation.Builder().nested(n -> n.path("attributes"))
                .aggregations(Map.of("value_ids", sub)).build();

        if(page == null){
            page = 1;
        }

        nativeQuery = builder.withQuery(q -> q.bool(bool -> {
                    bool.must(must -> {
                        if (StringUtils.isNotBlank(name)) {
                            must.match(m -> m.field("name").query(name));
                        } else {
                            must.matchAll(m -> m);
                        }
                        return must;
                    });
                    return bool;
                }))
                .withAggregation("brands", aggregationBrand)
                .withAggregation("attributes", aggregationAttr)
                .withFilter(f -> f.bool(bool -> {
                    if (StringUtils.isNotBlank(brand)) {
                        bool.filter(fBrand -> fBrand.term(t -> t.field("brand").value(brand)));
                    }
                    if (valueId != null) {
                        bool.filter(fNested -> fNested.nested(bNested -> bNested.path("attributes")
                                .query(qNested -> qNested.term(tNest -> tNest.field("attributes.value_id").value(valueId)))));
                    }
                    return bool;
                }))
                .withSort(Sort.by(Sort.Order.by("_score")))
                .withPageable(PageRequest.of(page - 1, 4))
                .build();

        return elasticsearchOperations.search(nativeQuery, SearchDetailDto.class);
    }
}

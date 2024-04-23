package org.example.converter;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Buckets;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.ext.DetailExt;
import org.example.dao.ext.DetailExtValues;
import org.example.dao.ext.DetailResultExt;
import org.example.search.dto.SearchDetailDto;
import org.example.search.dto.SearchDetailValueDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;

import java.awt.*;
import java.util.*;
import java.util.List;

@Slf4j
public class DetailConverter {

    public DetailResultExt dtoToDetailExt(List<SearchDetailDto> searchDetailDtos, Buckets<StringTermsBucket> brandBuckets){

        DetailResultExt result = new DetailResultExt();

        List<DetailExt> detailExtList = new ArrayList<>();
        Map<String, Long> brands = aggregations(brandBuckets);

        for (SearchDetailDto detailDto: searchDetailDtos){
            DetailExt detailExt = new DetailExt();
            detailExt.setId(detailDto.getId());
            detailExt.setBrand(detailDto.getBrand());
            detailExt.setOem(detailDto.getOem());
            detailExt.setName(detailDto.getName());

            if (detailDto.getAttributes() != null){
                List<DetailExtValues> detailExtValuesList = new ArrayList<>();
                for(SearchDetailValueDto valueDto: detailDto.getAttributes()){
                    DetailExtValues detailExtValues = new DetailExtValues();
                    detailExtValues.setAttribute_name(valueDto.getAttributeName());
                    detailExtValues.setValue(valueDto.getValue());
                    detailExtValuesList.add(detailExtValues);
                }
                detailExt.setValues(detailExtValuesList);
            }
            detailExtList.add(detailExt);
        }

        result.setTotal(detailExtList.size());
        result.setDetails(detailExtList);
        result.setBrands(brands);

        return result;
    }

    private Map<String, Long> aggregations (Buckets<StringTermsBucket> brandBuckets){

        Map<String, Long> brands = new HashMap<>();

        for(StringTermsBucket bucket: brandBuckets.array()){
            String key = bucket.key().stringValue();
            Long docCount = bucket.docCount();
            brands.put(key, docCount);
        }
        return brands;
    }
}

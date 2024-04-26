package org.example.converter;

import co.elastic.clients.elasticsearch._types.aggregations.Buckets;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import lombok.RequiredArgsConstructor;
import org.example.dao.ext.DetailExt;
import org.example.dao.ext.DetailExtValues;
import org.example.dao.ext.DetailResultExt;
import org.example.dao.ext.DetailResultExtValues;
import org.example.search.dto.SearchDetailDto;
import org.example.search.dto.SearchDetailValueDto;
import org.example.service.DetailTypeService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DetailConverter {

    private final DetailTypeService detailTypeService;

    public DetailResultExt dto2DetailResultExt(List<SearchDetailDto> searchDetailDtos,
                                                      Buckets<StringTermsBucket> brandBuckets,
                                                      Buckets<LongTermsBucket> attrBuckets,
                                                      long total,
                                                      Integer totalPages,
                                                      Integer page){

        DetailResultExt result = new DetailResultExt();

        List<DetailExt> detailExtList = new ArrayList<>();
        Map<String, Long> brands = brandBuckets2Map(brandBuckets);
        List<DetailResultExtValues> values = attrBuckets2Map(attrBuckets);

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

        result.setTotal(total);
        result.setDetails(detailExtList);
        result.setBrands(brands);
        result.setPage(page);
        result.setTotalPages(totalPages);
        result.setValues(values);

        return result;
    }

    private Map<String, Long> brandBuckets2Map (Buckets<StringTermsBucket> brandBuckets){

        Map<String, Long> brands = new HashMap<>();

        for(StringTermsBucket bucket: brandBuckets.array()){
            String key = bucket.key().stringValue();
            Long docCount = bucket.docCount();
            brands.put(key, docCount);
        }
        return brands;
    }

    private List<DetailResultExtValues> attrBuckets2Map(Buckets<LongTermsBucket> attrBuckets){

        List<DetailResultExtValues> values = new ArrayList<>();

        for (LongTermsBucket bucket: attrBuckets.array()){
            int id = Math.toIntExact(bucket.key());
            DetailResultExtValues resultExtValues = detailTypeService.findDetailTypeById(id);
            resultExtValues.setCount(bucket.docCount());
            values.add(resultExtValues);
        }

        return  values;
    }
}

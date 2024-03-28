package org.example.converter;

import org.example.dao.ValueRepository;
import org.example.entity.AttributeValue;
import org.example.entity.Detail;
import org.example.search.dto.SearchDetailDto;
import org.example.search.dto.SearchDetailValueDto;

import java.util.ArrayList;
import java.util.List;

public class DetailConverter {

    private ValueRepository valueRepository;
    public List<Detail> searchDtoToDetail (List<SearchDetailDto> searchDetailDtos){

        List<Detail> detailList = new ArrayList<>();

        for (SearchDetailDto detailDto: searchDetailDtos){
            List<AttributeValue> values = new ArrayList<>();
            Detail detail = new Detail();
            detail.setBrand(detailDto.getBrand());
            detail.setOem(detailDto.getOem());
            detail.setName(detailDto.getName());
            for (SearchDetailValueDto valueDto: detailDto.getAttributes()){
                AttributeValue attributeValue = new AttributeValue();
                attributeValue.setDetail(detail);
                values.add(attributeValue);
            }
            detail.setAttributeValues(values);
            detailList.add(detail);
        }
        return detailList;
    }
}

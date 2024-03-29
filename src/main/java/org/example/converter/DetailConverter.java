package org.example.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dao.ext.DetailExt;
import org.example.dao.ext.DetailExtValues;
import org.example.search.dto.SearchDetailDto;
import org.example.search.dto.SearchDetailValueDto;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class DetailConverter {

    public List<DetailExt> dtoToDetailExt(List<SearchDetailDto> searchDetailDtos){
        List<DetailExt> result = new ArrayList<>();

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
            result.add(detailExt);
        }

        return result;
    }
}

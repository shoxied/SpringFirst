package org.example.converter;

import org.example.dao.ext.DetailExt;
import org.example.entity.Detail;
import org.example.search.dto.SearchDetailDto;
import org.example.search.dto.SearchDetailValueDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DetailConverterTest {

    @InjectMocks
    DetailConverter detailConverter;

    @Test
    void dtoToExtConvert(){
        SearchDetailDto searchDetailDto = SearchDetailDto.builder()
                .id(0)
                .brand("brand")
                .oem("oem")
                .name("name")
                .attributes(List.of(SearchDetailValueDto.builder()
                        .id(1)
                        .attributeId(1)
                        .attributeName("attribute")
                        .valueId(1)
                        .value("value")
                        .build()))
                .build();

        List<DetailExt> actual = detailConverter.dtoToDetailExt(List.of(searchDetailDto));

        assertEquals(0 ,actual.get(0).getId());
        assertEquals("brand" ,actual.get(0).getBrand());
        assertEquals("oem" ,actual.get(0).getOem());
        assertEquals("name" ,actual.get(0).getName());
        assertEquals("attribute" ,actual.get(0).getValues().get(0).getAttribute_name());
        assertEquals("value" ,actual.get(0).getValues().get(0).getValue());
    }

}
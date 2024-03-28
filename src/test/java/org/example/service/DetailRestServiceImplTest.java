package org.example.service;

import org.example.dao.DetailRepository;
import org.example.dao.ValueRepository;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.Attribute;
import org.example.entity.AttributeValue;
import org.example.entity.Detail;
import org.example.entity.Value;
import org.example.search.dto.SearchDetailDto;
import org.example.search.repo.SearchDetailRepo;
import org.example.service.Impl.DetailRestServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DetailRestServiceImplTest {

    @Mock
    private DetailRepository detailRepository;

    @Mock
    private ValueRepository valueRepository;

    @Mock
    private SearchDetailRepo searchDetailRepo;

    @InjectMocks
    private DetailRestServiceImpl detailRestService;

    @Test
    void addNewDetail_save(){
        DetailUpdate detailUpdate = new DetailUpdate();
        detailUpdate.setName("name");
        detailUpdate.setOem("oem");
        detailUpdate.setBrand("brand");
        Detail expected = new Detail();

        when(detailRepository.save(any())).thenReturn(expected);

        Detail actual = detailRestService.addDetail(detailUpdate);

        assertSame(expected, actual);

        ArgumentCaptor<Detail> detailCaptor = ArgumentCaptor.forClass(Detail.class);
        Mockito.verify(detailRepository).save(detailCaptor.capture());
        Detail savedDetail = detailCaptor.getValue();
        assertEquals("name", savedDetail.getName());
        assertEquals("oem", savedDetail.getOem());
        assertEquals("brand", savedDetail.getBrand());
    }

    @Test
    void addNewDetail_attribute(){
        DetailUpdate detailUpdate = new DetailUpdate();
        detailUpdate.setName("name");
        detailUpdate.setOem("oem");
        detailUpdate.setBrand("brand");
        detailUpdate.setValues(List.of(10, 11));

        Value val10 = new Value();
        val10.setValue("val-10");
        when(valueRepository.findById(10)).thenReturn(Optional.of(val10));

        Value val11 = new Value();
        val11.setValue("val-11");
        when(valueRepository.findById(11)).thenReturn(Optional.of(val11));

        Detail detail = new Detail();
        when(detailRepository.save(any())).thenReturn(detail);

        SearchDetailDto searchDetailDto = new SearchDetailDto();
        when(searchDetailRepo.save(any())).thenReturn(searchDetailDto);

        detailRestService.addDetail(detailUpdate);

        assertEquals("val-10", detail.getAttributeValues().get(0).getValue().getValue());
        assertEquals("val-11", detail.getAttributeValues().get(1).getValue().getValue());

        Mockito.verify(valueRepository, times(2)).findById(any());
    }
}

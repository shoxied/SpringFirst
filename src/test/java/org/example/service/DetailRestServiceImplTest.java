package org.example.service;

import org.example.cache.RabbitListener;
import org.example.dao.DetailRepository;
import org.example.dao.ValueRepository;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.Detail;
import org.example.entity.Value;
import org.example.entity.ext.DetailList;
import org.example.search.dto.SearchDetailDto;
import org.example.search.repo.SearchDetailRepo;
import org.example.service.Impl.DetailRestServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.apache.coyote.http11.Constants.a;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DetailRestServiceImplTest {

    @Mock
    private DetailRepository detailRepository;
    @Mock
    private ValueRepository valueRepository;
    @Mock
    private SearchDetailRepo searchDetailRepo;

    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private RabbitListener rabbitListener;

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
        verify(detailRepository).save(detailCaptor.capture());
        Detail savedDetail = detailCaptor.getValue();
        assertEquals("name", savedDetail.getName());
        assertEquals("oem", savedDetail.getOem());
        assertEquals("brand", savedDetail.getBrand());
    }

    @Test
    void addNewDetails(){
        DetailUpdate detailUpdate1 = new DetailUpdate();
        detailUpdate1.setName("name1");
        detailUpdate1.setOem("oem1");
        detailUpdate1.setBrand("brand1");
        Detail expected1 = new Detail();
        expected1.setId(1);

        DetailUpdate detailUpdate2 = new DetailUpdate();
        detailUpdate2.setName("name2");
        detailUpdate2.setOem("oem2");
        detailUpdate2.setBrand("brand2");
        Detail expected2 = new Detail();
        expected2.setId(2);

        List<Detail> expected = new ArrayList<>();
        expected.add(expected1);
        expected.add(expected2);

        List<DetailUpdate> detailUpdates = new ArrayList<>();
        detailUpdates.add(detailUpdate1);
        detailUpdates.add(detailUpdate2);
        DetailList detailList = new DetailList();
        detailList.setDetails(detailUpdates);

        when(detailRepository.saveAll(any())).thenReturn(expected);

        List<Detail> actual = detailRestService.addDetails(detailList);
        assertEquals(1, actual.get(0).getId());
        assertEquals(2, actual.get(1).getId());

        ArgumentCaptor<List<Detail>> captor = ArgumentCaptor.forClass(List.class);
        verify(detailRepository).saveAll(captor.capture());

        assertEquals("name1", captor.getValue().get(0).getName());

        assertEquals("name2", captor.getValue().get(1).getName());
    }

    @Test
    void addNewDetail_attribute(){
        DetailUpdate detailUpdate = new DetailUpdate();
        detailUpdate.setValues(List.of(10, 11));

        Value val10 = new Value();
        when(valueRepository.findById(10)).thenReturn(Optional.of(val10));

        Value val11 = new Value();
        when(valueRepository.findById(11)).thenReturn(Optional.of(val11));

        Detail detail = new Detail();
        when(detailRepository.save(any())).thenReturn(detail);

        detailRestService.addDetail(detailUpdate);

        verify(rabbitTemplate).convertAndSend(RabbitListener.EXCHANGE, RabbitListener.ROUTING_KEY, 0);
        verify(valueRepository, times(2)).findById(any());
    }
}

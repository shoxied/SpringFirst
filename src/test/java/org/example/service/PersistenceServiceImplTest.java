package org.example.service;

import org.example.dao.DetailRepository;
import org.example.dao.ValueRepository;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.Detail;
import org.example.entity.Value;
import org.example.service.Impl.PersistenceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersistenceServiceImplTest {

    @Mock
    private DetailRepository detailRepository;
    @Mock
    private ValueRepository valueRepository;

    @InjectMocks
    private PersistenceServiceImpl persistenceService;

    @Test
    void save_detail(){

        Value value = Value.builder().build();

        DetailUpdate detailUpdate = DetailUpdate.builder()
                .brand("brand")
                .oem("oem")
                .name("name")
                .values(List.of(1))
                .build();

        when(valueRepository.findById(any())).thenReturn(Optional.ofNullable(value));

        persistenceService.saveDetail(detailUpdate);

        ArgumentCaptor<Detail> captor = ArgumentCaptor.forClass(Detail.class);
        verify(detailRepository).save(captor.capture());
        Detail detail = captor.getValue();
        assertEquals("brand", detail.getBrand());
        assertEquals("oem", detail.getOem());
        assertEquals("name", detail.getName());
        assertEquals(0, detail.getAttributeValues().get(0).getId());
    }
}
package org.example.cache;

import org.example.dao.DetailRepository;
import org.example.entity.Detail;
import org.example.search.dto.SearchDetailDto;
import org.example.search.repo.SearchDetailRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RabbitListenerTest {

    @Mock
    private DetailRepository detailRepository;
    @Mock
    private SearchDetailRepo searchDetailRepo;

    @InjectMocks
    private RabbitListener rabbitListener;

    @Test
    void receiveTest(){
        Detail detail = new Detail();
        detail.setId(0);
        detail.setBrand("brand");

        when(detailRepository.findById(any())).thenReturn(Optional.of(detail));

        rabbitListener.receive(0);

        verify(detailRepository).findById(0);
        ArgumentCaptor<SearchDetailDto> captor = ArgumentCaptor.forClass(SearchDetailDto.class);
        verify(searchDetailRepo).save(captor.capture());
        SearchDetailDto saved = captor.getValue();
        assertEquals(0, saved.getId());
        assertEquals("brand", saved.getBrand());
    }
}


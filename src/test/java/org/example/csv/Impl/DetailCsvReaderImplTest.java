package org.example.csv.Impl;

import org.example.dao.DetailRepository;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.Detail;
import org.example.entity.ext.DetailList;
import org.example.service.DetailRestService;
import org.example.service.Impl.DetailRestServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DetailCsvReaderImplTest {

    @Mock
    private DetailRestServiceImpl detailRestService;

    @InjectMocks
    private DetailCsvReaderImpl detailCsvReader;

    @Test
    void csv_import() throws IOException {

        detailCsvReader.read(IOUtils.toInputStream(
                """
                brand,oem,name
                brand1,oem1,name1
                """));

        ArgumentCaptor<DetailList> captor = ArgumentCaptor.forClass(DetailList.class);
        verify(detailRestService).addDetails(captor.capture());
        DetailList detailList = captor.getValue();
        assertEquals("brand1", detailList.getDetails().get(0).getBrand());
        assertEquals("oem1", detailList.getDetails().get(0).getOem());
        assertEquals("name1", detailList.getDetails().get(0).getName());
    }
}
package org.example.csv.Impl;

import org.example.dao.ext.DetailUpdate;
import org.example.entity.ext.DetailList;
import org.example.service.DetailRestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DetailCsvReaderImplTest {

    @Mock
    private DetailRestService detailRestService;

    @InjectMocks
    private DetailCsvReaderImpl detailCsvReader;

    @Test
    void csv_import() throws IOException {

        detailCsvReader.read(IOUtils.toInputStream(
                """
                brand,oem,name
                brand1,oem1,name1,1,2
                """));

        ArgumentCaptor<DetailUpdate> captor = ArgumentCaptor.forClass(DetailUpdate.class);
        verify(detailRestService).addDetail(captor.capture());
        DetailUpdate update = captor.getValue();
        assertEquals("brand1", update.getBrand());
        assertEquals("oem1", update.getOem());
        assertEquals("name1", update.getName());
        assertEquals(List.of(1, 2), update.getValues());
    }

    @Test
    void csv_import_many() throws IOException {

        detailCsvReader.read(IOUtils.toInputStream(
                """
                brand,oem,name
                brand1,oem1,name1,1,2
                brand2,oem2,name2,3,4
                brand3,oem3,name3,5,6
                """));

        ArgumentCaptor<DetailUpdate> captor = ArgumentCaptor.forClass(DetailUpdate.class);
        verify(detailRestService, times(3)).addDetail(captor.capture());
        assertEquals("brand1", captor.getAllValues().get(0).getBrand());
        assertEquals("oem1", captor.getAllValues().get(0).getOem());
        assertEquals("name1", captor.getAllValues().get(0).getName());
        assertEquals(List.of(1, 2), captor.getAllValues().get(0).getValues());

        assertEquals("brand2", captor.getAllValues().get(1).getBrand());
        assertEquals("oem2", captor.getAllValues().get(1).getOem());
        assertEquals("name2", captor.getAllValues().get(1).getName());
        assertEquals(List.of(3, 4), captor.getAllValues().get(1).getValues());

        assertEquals("brand3", captor.getAllValues().get(2).getBrand());
        assertEquals("oem3", captor.getAllValues().get(2).getOem());
        assertEquals("name3", captor.getAllValues().get(2).getName());
        assertEquals(List.of(5, 6), captor.getAllValues().get(2).getValues());
    }
}
package org.example.csv.Impl;


import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.example.csv.DetailCsvReader;
import org.example.dao.ext.DetailUpdate;
import org.example.service.DetailRestService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DetailCsvReaderImpl implements DetailCsvReader {

    private final DetailRestService detailRestService;
    @Override
    @Transactional
    public void read(InputStream inputStream) throws IOException {
        CSVParser csvParser = new CSVParser(new InputStreamReader(inputStream), CSVFormat.DEFAULT.builder().setHeader().build());
        csvParser.forEach(a->{
            DetailUpdate detailUpdate = new DetailUpdate();
            String brand = a.get(0);
            String oem = a.get(1);
            String name = a.get(2);
            List<Integer> attributeValues = new ArrayList<>();
            if (a.size() > 3) {
                 for (int s = 3; s < a.size(); s++){
                     attributeValues.add(Integer.valueOf(a.get(s)));
                 }
            }
            detailUpdate.setBrand(brand);
            detailUpdate.setOem(oem);
            detailUpdate.setName(name);
            detailUpdate.setValues(attributeValues);
            detailRestService.addDetail(detailUpdate);
        });
    }
}

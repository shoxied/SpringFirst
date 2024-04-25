package org.example.csv.Impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.example.csv.DetailCsvReader;
import org.example.dao.AttributeRepository;
import org.example.dao.ValueRepository;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.Attribute;
import org.example.entity.AttributeValue;
import org.example.entity.Value;
import org.example.entity.ext.DetailList;
import org.example.service.DetailRestService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class DetailCsvReaderImpl implements DetailCsvReader {

    private final DetailRestService detailRestService;
    private final AttributeRepository attributeRepository;

    @Override
    public void read(InputStream inputStream) throws IOException {
        CSVParser csvParser = new CSVParser(new InputStreamReader(inputStream), CSVFormat.DEFAULT.builder().setHeader().build());
        List<DetailUpdate> updates = new ArrayList<>();
        csvParser.forEach(a->{
            log.info("Start parsing");
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
            updates.add(detailUpdate);
            log.info("End parsing");
        });
        DetailList detailList = new DetailList();
        detailList.setDetails(updates);
        detailRestService.addDetails(detailList);
    }

    @Override
    public void readAttrValues(InputStream inputStream) throws IOException {
        CSVParser csvParser = new CSVParser(new InputStreamReader(inputStream), CSVFormat.DEFAULT.builder().setHeader().build());
        List<DetailUpdate> updates = new ArrayList<>();
        csvParser.forEach(a->{
            log.info("Start parsing");
            DetailUpdate detailUpdate = new DetailUpdate();
            String brand = a.get(0);
            String oem = a.get(1);
            String name = a.get(2);
            List<Integer> attributeValues = new ArrayList<>();
            if (a.size() > 3) {
                for (int s = 3; s < a.size() - 1; s++){
                    new Attribute();
                    Attribute attribute;
                    attribute = attributeRepository.findByName(a.get(s));
                    for (Value value: attribute.getValues()){
                        if(Objects.equals(value.getValue(), a.get(s + 1))){
                            attributeValues.add(value.getId());
                        }
                    }
                }
            }
            detailUpdate.setBrand(brand);
            detailUpdate.setOem(oem);
            detailUpdate.setName(name);
            detailUpdate.setValues(attributeValues);
            updates.add(detailUpdate);
            log.info("End parsing");
        });
        DetailList detailList = new DetailList();
        detailList.setDetails(updates);
        detailRestService.addDetails(detailList);
    }
}

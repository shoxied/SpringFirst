package org.example.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.DetailRepository;
import org.example.dao.ValueRepository;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.AttributeValue;
import org.example.entity.Detail;
import org.example.service.PersistenceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersistenceServiceImpl implements PersistenceService {

    private final DetailRepository detailRepository;
    private final ValueRepository valueRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Detail saveDetail(DetailUpdate update) {

        List<AttributeValue> values = new ArrayList<>();

        if (update.getValues() != null){
            for (Integer valueId: update.getValues()){
                values.add(AttributeValue.builder()
                        .value(valueRepository.findById(valueId).get())
                        .build());
            }
        }

        Detail savedDetail = Detail.builder()
                .brand(update.getBrand())
                .oem(update.getOem())
                .name(update.getName())
                .attributeValues(values)
                .build();

        if(detailRepository.findByBrand(savedDetail.getBrand()).size() != 0 && detailRepository.findByOem(savedDetail.getOem()).size() != 0){
            log.warn("not unique detail identifier, object didn't saved");
            return null;
        }
        else {
            return detailRepository.save(savedDetail);
        }
    }
}

package org.example.service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.dao.DetailRepository;
import org.example.dao.ValueRepository;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.AttributeValue;
import org.example.entity.Detail;
import org.example.service.DetailRestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class DetailRestServiceImpl implements DetailRestService {

    private final DetailRepository detailRepository;
    private final ValueRepository valueRepository;

    @Override
    public List<Detail> getDetails() {
        return StreamSupport.stream(detailRepository.findAll().spliterator(), false).toList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Detail addDetail(DetailUpdate update) {

        List<AttributeValue> values = new ArrayList<>();

        Detail detail = detailRepository.save(Detail.builder()
                .brand(update.getBrand())
                .oem(update.getOem())
                .name(update.getName())
                .build());

        if (update.getValues() != null){
            for (Integer valueId: update.getValues()){
                values.add(AttributeValue.builder()
                        .detail(detail)
                        .value(valueRepository.findById(valueId).get())
                        .build());
            }
        }

        detail.setAttributeValues(values);

        return detail;
    }
}

package org.example.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.ValueRepository;
import org.example.dao.ext.DetailResultExtValues;
import org.example.entity.Value;
import org.example.service.DetailTypeService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DetailTypeServiceImpl implements DetailTypeService {

    private final ValueRepository valueRepository;

    @Override
    @Cacheable(value = "codetable")
    public DetailResultExtValues findDetailTypeById(int id) {

        DetailResultExtValues resultValue = new DetailResultExtValues();

        Optional<Value> value = valueRepository.findById(id);

        resultValue.setAttributeId(value.get().getAttribute().getId());
        resultValue.setAttributeName(value.get().getAttribute().getName());
        resultValue.setValueId(value.get().getId());
        resultValue.setValue(value.get().getValue());

        return resultValue;
    }
}

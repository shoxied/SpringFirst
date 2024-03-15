package org.example.dao;

import org.example.entity.AttributeValue;
import org.springframework.data.repository.CrudRepository;

public interface AttributeValueRepository extends CrudRepository<AttributeValue, Integer> {
}

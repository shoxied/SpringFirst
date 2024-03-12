package org.example.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.example.entity.AttributeValue;
import org.springframework.data.repository.CrudRepository;

public interface AttributeValueRepository extends CrudRepository<AttributeValue, Integer> {
}

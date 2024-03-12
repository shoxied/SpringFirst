package org.example.dao;

import org.example.entity.Attribute;
import org.springframework.data.repository.CrudRepository;

public interface AttributeRepository extends CrudRepository<Attribute, Integer> {
}

package org.example.dao;

import org.example.entity.Attribute;
import org.example.entity.Value;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ValueRepository extends CrudRepository<Value, Integer> {
    List<Value> findByAttribute(Attribute attribute);
}

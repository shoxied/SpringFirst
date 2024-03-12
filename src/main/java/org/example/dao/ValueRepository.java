package org.example.dao;

import org.example.entity.Value;
import org.springframework.data.repository.CrudRepository;

public interface ValueRepository extends CrudRepository<Value, Integer> {
}

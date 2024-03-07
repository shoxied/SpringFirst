package org.example.dao;

import org.example.entity.Detail;
import org.springframework.data.repository.CrudRepository;

public interface DetailRepository extends CrudRepository<Detail, Integer> {
}

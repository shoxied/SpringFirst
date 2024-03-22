package org.example.dao;

import org.example.entity.Detail;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.repository.CrudRepository;


public interface DetailRepository extends CrudRepository<Detail, Integer> {
}

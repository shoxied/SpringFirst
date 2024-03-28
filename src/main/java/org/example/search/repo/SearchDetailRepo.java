package org.example.search.repo;

import org.example.search.dto.SearchDetailDto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchDetailRepo extends ElasticsearchRepository<SearchDetailDto, Integer> {
}

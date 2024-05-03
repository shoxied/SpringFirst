package org.example.service;

import org.example.search.dto.SearchDetailDto;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface SearchService {

    SearchHits<SearchDetailDto> getDetails(String name, String brand, Integer valueId, Integer page);
}

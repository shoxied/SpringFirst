package org.example.service;

import org.example.dao.ext.DetailUpdate;
import org.example.entity.Detail;
import org.example.search.dto.SearchDetailDto;

import java.util.List;

public interface DetailRestService {

    List<Detail> getDetails(String name);

    Detail addDetail(DetailUpdate update);
}

package org.example.service;

import org.example.dao.ext.DetailExt;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.Detail;
import org.example.search.dto.SearchDetailDto;

import java.util.List;

public interface DetailRestService {

    List<DetailExt> getDetails(String name);

    Detail addDetail(DetailUpdate update);
}

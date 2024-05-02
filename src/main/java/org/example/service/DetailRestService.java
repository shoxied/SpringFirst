package org.example.service;

import org.example.dao.ext.DetailExt;
import org.example.dao.ext.DetailResultExt;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.Detail;
import org.example.entity.ext.DetailList;
import org.example.search.dto.SearchDetailDto;

import java.util.List;
import java.util.Set;

public interface DetailRestService {

    DetailResultExt getDetails(String name, String brand, Integer valueId, Integer page);

    Detail addDetail(DetailUpdate update);

    List<Detail> addDetails(DetailList update);
}

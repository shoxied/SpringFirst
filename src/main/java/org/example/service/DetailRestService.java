package org.example.service;

import org.example.dao.ext.DetailUpdate;
import org.example.entity.Detail;

import java.util.List;

public interface DetailRestService {

    List<Detail> getDetails();

    Detail addDetail(DetailUpdate update);
}

package org.example.service;

import org.example.dao.ext.DetailUpdate;
import org.example.entity.Detail;


public interface PersistenceService {

    Detail saveDetail(DetailUpdate update);
}

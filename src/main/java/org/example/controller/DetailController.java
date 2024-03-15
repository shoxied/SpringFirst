package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.Detail;
import org.example.service.DetailRestService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/my/rest/details")
@RequiredArgsConstructor
public class DetailController {

    private final DetailRestService detailRestService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Detail> details(){
        return detailRestService.getDetails();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public Detail addDetail(@RequestBody DetailUpdate update){
        return detailRestService.addDetail(update);
    }
}

package org.example.controller;

import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.example.csv.DetailCsvReader;
import org.example.dao.ext.DetailExt;
import org.example.dao.ext.DetailResultExt;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.Detail;
import org.example.entity.ext.DetailList;
import org.example.search.dto.SearchDetailDto;
import org.example.service.DetailRestService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/my/rest")
@RequiredArgsConstructor
public class DetailController {

    private final DetailRestService detailRestService;
    private final DetailCsvReader detailCsvReader;


    @GetMapping(value = "details",produces = MediaType.APPLICATION_JSON_VALUE)
    public DetailResultExt details(@RequestParam(name = "name", required = false) String name){
        return detailRestService.getDetails(name);
    }

    @PostMapping(value = "newDetail",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public Detail addDetail(@RequestBody DetailUpdate update){
        return detailRestService.addDetail(update);
    }

    @PostMapping(value = "newDetails",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Detail> addDetails(@RequestBody DetailList update){
        return detailRestService.addDetails(update);
    }

    @PostMapping(value = "csv", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void importCSV(@RequestPart("file") MultipartFile file) throws IOException {
        detailCsvReader.read(file.getInputStream());
    }

    @PostMapping(value = "csvValues", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void importCSVAttrValues(@RequestPart("file") MultipartFile file) throws IOException {
        detailCsvReader.readAttrValues(file.getInputStream());
    }
}

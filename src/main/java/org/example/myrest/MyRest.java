package org.example.myrest;

import lombok.RequiredArgsConstructor;
import org.example.dao.DetailRepository;
import org.example.entity.Detail;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("api/my/rest")
@RequiredArgsConstructor
public class MyRest {

    private final DetailRepository detailRepository;

    @GetMapping(value = "hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public String hello(){
        return "Hello";
    }

    @GetMapping(value = "details", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Detail> details(){
        return StreamSupport.stream(detailRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}

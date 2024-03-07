package org.example.myrest;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/my/rest")
public class MyRest {

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String hello(){
        return "Hello";
    }

}

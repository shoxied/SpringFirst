package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dao.ext.DetailExt;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.Detail;
import org.example.service.DetailRestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DetailControllerTest {

    @Mock
    private DetailRestService detailRestService;

    @InjectMocks
    private DetailController detailController;

    private MockMvc mvc;

    @BeforeEach
    void setUp(){
        mvc = MockMvcBuilders.standaloneSetup(detailController).setValidator(new Validator() {
            @Override
            public boolean supports(Class<?> clazz) {return false;}
            @Override
            public void validate(Object target, Errors errors) {}
        }).build();
    }

    @Test
    void getAllDetails() throws Exception{
        when(detailRestService.getDetails(any())).thenReturn(List.of(DetailExt.builder()
                .id(0)
                .brand("brand")
                .oem("oem")
                .name("name")
                .build()));
        mvc.perform(MockMvcRequestBuilders.get("/api/my/rest/details")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(0)))
                .andExpect(jsonPath("$[0].brand", is("brand")));
    }

    @Test
    void addNewDetail() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        DetailUpdate detailUpdate = new DetailUpdate();
        detailUpdate.setName("name");
        detailUpdate.setOem("oem");
        detailUpdate.setBrand("brand");
        mvc.perform(MockMvcRequestBuilders.post("/api/my/rest/newDetail")
            .content(mapper.writeValueAsString(detailUpdate))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(detailRestService).addDetail(detailUpdate);
    }
}
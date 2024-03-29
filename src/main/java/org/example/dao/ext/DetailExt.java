package org.example.dao.ext;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DetailExt {

    private Integer id;
    private String brand;
    private String oem;
    private String name;
    private List<DetailExtValues> values;
}

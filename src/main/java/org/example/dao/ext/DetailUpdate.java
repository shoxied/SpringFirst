package org.example.dao.ext;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.example.entity.AttributeValue;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class DetailUpdate {

    private String brand;

    private String oem;

    private String name;

    private List<Integer> values;

}

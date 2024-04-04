package org.example.entity.ext;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import lombok.*;
import org.example.dao.ext.DetailUpdate;
import org.example.entity.Detail;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public class DetailList {

    private List<DetailUpdate> details;
}

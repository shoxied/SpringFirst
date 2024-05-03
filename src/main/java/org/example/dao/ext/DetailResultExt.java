package org.example.dao.ext;

import lombok.*;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class DetailResultExt {

    private long total;
    private Integer totalPages;
    private Integer page;

    private List<DetailExt> details;
    private Map<String, Long> brands;
    private List<DetailResultExtValues> values;
}
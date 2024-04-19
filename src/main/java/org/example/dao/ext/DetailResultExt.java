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
    private int total;
    private List<DetailExt> details;
    private Map<String, Integer> attributeCount;
}
package org.example.dao.ext;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class DetailResultExtValues {

    private int attributeId;
    private String attributeName;
    private int valueId;
    private String value;
    private Long count;
}

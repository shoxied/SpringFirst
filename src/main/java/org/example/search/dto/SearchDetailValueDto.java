package org.example.search.dto;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SearchDetailValueDto {

    @Field(name="id", type = FieldType.Integer, index = false)
    private int id;

    @Field(name="attribute_id", type = FieldType.Integer)
    private int attributeId;

    @Field(name="attribute_name", type = FieldType.Text, index = false)
    private String attributeName;

    @Field(name="value_id", type = FieldType.Integer)
    private int valueId;

    @Field(name="string_value", type = FieldType.Text)
    private String value;

}

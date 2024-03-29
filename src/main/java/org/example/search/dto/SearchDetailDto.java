package org.example.search.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Document(indexName = "detail", writeTypeHint = WriteTypeHint.DEFAULT)
@Setting(settingPath = "stemmer/stemmer-settings.json")
public class SearchDetailDto {

    @Id
    @Field(name = "id", type = FieldType.Integer)
    private int id;

    @Field(name = "brand", type = FieldType.Keyword)
    private String brand;

    @Field(name = "oem", type = FieldType.Keyword)
    private String oem;

    @Field(name = "name", type = FieldType.Text, analyzer = "en-ru")
    private String name;

    @Field(name = "attributes", type = FieldType.Nested)
    private List<SearchDetailValueDto> attributes;
}

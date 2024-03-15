package org.example.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Detail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String oem;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "detail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AttributeValue> attributeValues = new ArrayList<>();
}

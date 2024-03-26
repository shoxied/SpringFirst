package org.example.entity;


import jakarta.persistence.*;
import lombok.*;

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

    @OneToMany(mappedBy = "detail", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<AttributeValue> attributeValues = new ArrayList<>();

    @PrePersist
    public void preUpdate() {
        if (attributeValues != null) {
            attributeValues.forEach(attributeValue -> attributeValue.setDetail(this));
        }
    }
}

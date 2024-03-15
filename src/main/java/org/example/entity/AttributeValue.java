package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JsonIgnore
    private Detail detail;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "value_id", nullable = false)
    private Value value;
}

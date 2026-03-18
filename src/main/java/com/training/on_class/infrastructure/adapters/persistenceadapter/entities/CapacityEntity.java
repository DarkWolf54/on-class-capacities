package com.training.on_class.infrastructure.adapters.persistenceadapter.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("capacity")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CapacityEntity {
    @Id
    private Long id;
    private String name;
    private String description;
}
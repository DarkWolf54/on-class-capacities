package com.training.on_class.domain.model;

import com.training.on_class.domain.exceptions.BusinessException;
import java.util.List;

public class Capacity {

    private final Long id;
    private final String name;
    private final String description;
    private final List<Long> technologyIds;

    public Capacity(Long id, String name, String description, List<Long> technologyIds) {
        this.id = id;
        this.name = name;
        this.description = description;

        if (technologyIds == null || technologyIds.isEmpty()) {
            throw new BusinessException("La capacidad debe tener tecnologías asociadas.");
        }

        if (technologyIds.size() < 3) {
            throw new BusinessException("Una capacidad debe tener al menos 3 tecnologías.");
        }

        if (technologyIds.size() > 20) {
            throw new BusinessException("Una capacidad no puede tener más de 20 tecnologías.");
        }

        long uniqueCount = technologyIds.stream().distinct().count();
        if (uniqueCount != technologyIds.size()) {
            throw new BusinessException("La capacidad no puede tener tecnologías duplicadas.");
        }

        this.technologyIds = technologyIds;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<Long> getTechnologyIds() { return technologyIds; }
}

package com.training.on_class.domain.model;

import com.training.on_class.domain.exceptions.BusinessException;
import java.util.List;

public class Capacity {

    private final Long id;
    private final String name;
    private final String description;
    private final List<Long> technologyIds;
    private List<Technology> technologies;

    public Capacity(Long id, String name, String description, List<Long> technologyIds) {
        this.id = id;
        this.name = name;
        this.description = description;

        validateTechnologies(technologyIds);

        this.technologyIds = technologyIds;
    }

    public Capacity(Long id, String name, String description, List<Technology> technologies, boolean isRead) {
        this.id = id;
        this.name = name;
        this.description = description;

        List<Long> extractedIds = technologies != null ?
          technologies.stream().map(Technology::getId).toList() : List.of();

        if (!isRead) {
            validateTechnologies(extractedIds);
        }

        this.technologyIds = extractedIds;
        this.technologies = technologies;
    }

    private void validateTechnologies(List<Long> techIds) {
        if (techIds == null || techIds.isEmpty()) {
            throw new BusinessException("La capacidad debe tener tecnologías asociadas.");
        }

        if (techIds.size() < 3) {
            throw new BusinessException("Una capacidad debe tener al menos 3 tecnologías.");
        }

        if (techIds.size() > 20) {
            throw new BusinessException("Una capacidad no puede tener más de 20 tecnologías.");
        }

        long uniqueCount = techIds.stream().distinct().count();
        if (uniqueCount != techIds.size()) {
            throw new BusinessException("La capacidad no puede tener tecnologías duplicadas.");
        }
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<Long> getTechnologyIds() { return technologyIds; }
    public List<Technology> getTechnologies() { return technologies; }

}
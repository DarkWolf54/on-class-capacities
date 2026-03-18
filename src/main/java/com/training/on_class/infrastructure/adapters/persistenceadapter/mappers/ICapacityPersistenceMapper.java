package com.training.on_class.infrastructure.adapters.persistenceadapter.mappers;

import com.training.on_class.domain.model.Capacity;
import com.training.on_class.infrastructure.adapters.persistenceadapter.entities.CapacityEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICapacityPersistenceMapper {

    CapacityEntity toEntity(Capacity capacity);

    Capacity toDomain(CapacityEntity entity, List<Long> technologyIds);
}

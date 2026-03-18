package com.training.on_class.infrastructure.adapters.persistenceadapter.mappers;

import com.training.on_class.domain.model.Capacity;
import com.training.on_class.infrastructure.adapters.persistenceadapter.entities.CapacityEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICapacityPersistenceMapper {

    CapacityEntity toEntity(Capacity capacity);

    default Capacity toDomain(CapacityEntity entity, List<Long> technologyIds) {
        if (entity == null) {
            return null;
        }
        return new Capacity(
          entity.getId(),
          entity.getName(),
          entity.getDescription(),
          technologyIds
        );
    }
}
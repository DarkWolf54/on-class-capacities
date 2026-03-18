package com.training.on_class.infrastructure.entrypoints.rest.mapper;

import com.training.on_class.domain.model.Capacity;
import com.training.on_class.infrastructure.entrypoints.rest.dto.request.CapacityRequest;
import com.training.on_class.infrastructure.entrypoints.rest.dto.response.CapacityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ICapacityRestMapper {

    @Mapping(target = "id", ignore = true)
    Capacity toDomain(CapacityRequest request);

    CapacityResponse toResponse(Capacity capacity);
}

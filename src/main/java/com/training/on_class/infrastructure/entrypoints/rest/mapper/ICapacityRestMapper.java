package com.training.on_class.infrastructure.entrypoints.rest.mapper;

import com.training.on_class.domain.model.Capacity;
import com.training.on_class.domain.model.PaginatedList;
import com.training.on_class.infrastructure.entrypoints.rest.dto.request.CapacityRequest;
import com.training.on_class.infrastructure.entrypoints.rest.dto.response.CapacityResponse;
import com.training.on_class.infrastructure.entrypoints.rest.dto.response.PaginationResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICapacityRestMapper {

    default Capacity toDomain(CapacityRequest request) {
        if (request == null) {
            return null;
        }
        return new Capacity(
          null,
          request.getName(),
          request.getDescription(),
          request.getTechnologyIds()
        );
    }

    CapacityResponse toResponse(Capacity capacity);

    default PaginationResponse<CapacityResponse> toPaginatedResponse(PaginatedList<Capacity> paginatedList) {
        List<CapacityResponse> dtoList = paginatedList.getData().stream()
          .map(this::toResponse)
          .toList();

        return new PaginationResponse<>(
          dtoList,
          paginatedList.getPage(),
          paginatedList.getSize(),
          paginatedList.getTotalElements(),
          paginatedList.getTotalPages()
        );
    }
}
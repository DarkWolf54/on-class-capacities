package com.training.on_class.infrastructure.entrypoints.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Objeto que representa los datos de una capacidad registrada")
public class CapacityResponse {

    private Long id;
    private String name;
    private String description;

    private List<Long> technologyIds;

    private List<TechnologyResponse> technologies;
}

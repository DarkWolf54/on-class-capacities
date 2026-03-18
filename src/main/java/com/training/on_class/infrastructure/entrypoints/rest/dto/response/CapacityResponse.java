package com.training.on_class.infrastructure.entrypoints.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "Objeto que representa los datos de una capacidad registrada")
public class CapacityResponse {

    @Schema(description = "Identificador único de la capacidad", example = "1")
    private Long id;

    @Schema(description = "Nombre de la capacidad", example = "Desarrollador Backend")
    private String name;

    @Schema(description = "Descripción de la capacidad", example = "Capacidad para construir APIs y lógica de servidor.")
    private String description;

    @Schema(description = "Lista de IDs de las tecnologías asociadas", example = "[1, 2, 3]")
    private List<Long> technologyIds;
}

package com.training.on_class.infrastructure.entrypoints.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto que representa una tecnología asociada a una capacidad")
public class TechnologyResponse {

    @Schema(description = "Identificador único de la tecnología", example = "1")
    private Long id;

    @Schema(description = "Nombre de la tecnología", example = "Java")
    private String name;
}

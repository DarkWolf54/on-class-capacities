package com.training.on_class.infrastructure.entrypoints.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Objeto que representa la petición para crear una nueva capacidad")
public class CapacityRequest {

    @Schema(description = "Nombre de la capacidad", example = "Desarrollador Backend", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder los 50 caracteres")
    private String name;

    @Schema(description = "Descripción de la capacidad", example = "Capacidad para construir APIs y lógica de servidor.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 90)
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 90, message = "La descripción no puede exceder los 90 caracteres")
    private String description;

    @Schema(description = "Lista de IDs de las tecnologías asociadas (mínimo 3, máximo 20)", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La lista de tecnologías es obligatoria")
    @Size(min = 3, max = 20, message = "Una capacidad debe tener entre 3 y 20 tecnologías")
    private List<Long> technologyIds;
}

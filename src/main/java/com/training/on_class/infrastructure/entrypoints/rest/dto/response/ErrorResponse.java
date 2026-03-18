package com.training.on_class.infrastructure.entrypoints.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Estructura estándar para todas las respuestas de error devueltas por la API")
public class ErrorResponse {

    @Schema(description = "Mensaje descriptivo del error (regla de negocio, validación o error interno)",
      example = "El nombre es obligatorio")
    private String message;

    @Schema(description = "Código de estado HTTP devuelto",
      example = "400")
    private int status;
}
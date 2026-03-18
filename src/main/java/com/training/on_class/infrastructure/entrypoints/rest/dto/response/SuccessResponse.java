package com.training.on_class.infrastructure.entrypoints.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Estructura genérica estándar para todas las respuestas exitosas de la API")
public class SuccessResponse<T> {

    @Schema(description = "Mensaje descriptivo sobre el resultado de la operación",
      example = "Operación realizada exitosamente")
    private String message;

    @Schema(description = "Carga útil (payload) que contiene los datos de la respuesta. Su estructura depende del endpoint consumido.")
    private T data;
}
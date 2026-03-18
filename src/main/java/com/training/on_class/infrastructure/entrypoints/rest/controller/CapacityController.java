package com.training.on_class.infrastructure.entrypoints.rest.controller;

import com.training.on_class.domain.ports.inbound.ICapacityServicePort;
import com.training.on_class.infrastructure.entrypoints.rest.dto.request.CapacityRequest;
import com.training.on_class.infrastructure.entrypoints.rest.dto.response.CapacityResponse;
import com.training.on_class.infrastructure.entrypoints.rest.dto.response.ErrorResponse;
import com.training.on_class.infrastructure.entrypoints.rest.dto.response.SuccessResponse;
import com.training.on_class.infrastructure.entrypoints.rest.mapper.ICapacityRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/capacities")
@RequiredArgsConstructor
@Tag(name = "Capacity", description = "Endpoints para la gestión de capacidades del bootcamp")
public class CapacityController {

    private final ICapacityServicePort capacityServicePort;
    private final ICapacityRestMapper mapper;

    @Operation(summary = "Crear una nueva capacidad",
      description = "Registra una capacidad validando que tenga entre 3 y 20 tecnologías y que estas existan en el sistema.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Capacidad creada exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
      @ApiResponse(responseCode = "400", description = "Error de validación o tecnologías inexistentes/duplicadas",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Usuario no autenticado",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "El usuario no tiene el rol necesario (ADMIN)",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SuccessResponse<CapacityResponse>> createCapacity(
      @Valid @RequestBody Mono<CapacityRequest> requestMono) {

        return requestMono
          .map(mapper::toDomain)
          .flatMap(capacityServicePort::saveCapacity)
          .map(mapper::toResponse)
          .map(dto -> new SuccessResponse<>("Capacidad creada exitosamente", dto));
    }
}

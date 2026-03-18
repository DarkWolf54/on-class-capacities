package com.training.on_class.infrastructure.entrypoints.rest.controller;

import com.training.on_class.domain.exceptions.BusinessException;
import com.training.on_class.domain.model.Capacity;
import com.training.on_class.domain.ports.inbound.ICapacityServicePort;
import com.training.on_class.infrastructure.entrypoints.rest.dto.request.CapacityRequest;
import com.training.on_class.infrastructure.entrypoints.rest.dto.response.CapacityResponse;
import com.training.on_class.infrastructure.entrypoints.rest.exception.GlobalExceptionHandler;
import com.training.on_class.infrastructure.entrypoints.rest.mapper.ICapacityRestMapper;
import com.training.on_class.infrastructure.entrypoints.rest.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = CapacityController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class CapacityControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ICapacityServicePort capacityServicePort;

    @MockitoBean
    private ICapacityRestMapper mapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCapacity_Success() {
        // Arrange
        CapacityRequest request = new CapacityRequest();
        request.setName("Backend Developer");
        request.setDescription("Capacidad para crear APIs");
        request.setTechnologyIds(Arrays.asList(1L, 2L, 3L));

        Capacity mappedCapacity = new Capacity(null, "Backend Developer", "Capacidad para crear APIs", Arrays.asList(1L, 2L, 3L));
        Capacity savedCapacity = new Capacity(1L, "Backend Developer", "Capacidad para crear APIs", Arrays.asList(1L, 2L, 3L));
        CapacityResponse responseDto = new CapacityResponse(1L, "Backend Developer", "Capacidad para crear APIs", Arrays.asList(1L, 2L, 3L));

        when(mapper.toDomain(any(CapacityRequest.class)))
          .thenReturn(mappedCapacity);

        when(capacityServicePort.saveCapacity(any(Capacity.class)))
          .thenReturn(Mono.just(savedCapacity));

        when(mapper.toResponse(any(Capacity.class)))
          .thenReturn(responseDto);

        // Act & Assert
        webTestClient.post()
          .uri("/api/v1/capacities")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(request)
          .exchange()
          .expectStatus().isCreated()
          .expectBody()
          .jsonPath("$.message").isEqualTo("Capacidad creada exitosamente")
          .jsonPath("$.data.id").isEqualTo(1)
          .jsonPath("$.data.name").isEqualTo("Backend Developer")
          .jsonPath("$.data.description").isEqualTo("Capacidad para crear APIs");

        verify(mapper, times(1)).toDomain(any(CapacityRequest.class));
        verify(capacityServicePort, times(1)).saveCapacity(any(Capacity.class));
        verify(mapper, times(1)).toResponse(any(Capacity.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCapacity_Returns400_WhenNameIsBlank() {
        // Arrange
        CapacityRequest request = new CapacityRequest();
        request.setName("");
        request.setDescription("Capacidad para crear APIs");
        request.setTechnologyIds(Arrays.asList(1L, 2L, 3L));

        // Act & Assert
        webTestClient.post()
          .uri("/api/v1/capacities")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(request)
          .exchange()
          .expectStatus().isBadRequest()
          .expectBody()
          .jsonPath("$.status").isEqualTo(400)
          .jsonPath("$.message").value(msg -> assertThat((String)msg).contains("El nombre es obligatorio"));

        verify(mapper, never()).toDomain(any());
        verify(capacityServicePort, never()).saveCapacity(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCapacity_Returns400_WhenTechnologiesDoNotExist() {
        // Arrange
        CapacityRequest request = new CapacityRequest();
        request.setName("Backend Developer");
        request.setDescription("Capacidad para crear APIs");
        request.setTechnologyIds(Arrays.asList(1L, 2L, 99L));

        Capacity mappedCapacity = new Capacity(null, "Backend Developer", "Capacidad para crear APIs", Arrays.asList(1L, 2L, 99L));

        when(mapper.toDomain(any(CapacityRequest.class)))
          .thenReturn(mappedCapacity);

        when(capacityServicePort.saveCapacity(any(Capacity.class)))
          .thenReturn(Mono.error(new BusinessException("Una o más tecnologías proporcionadas no existen en el sistema.")));

        // Act & Assert
        webTestClient.post()
          .uri("/api/v1/capacities")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(request)
          .exchange()
          .expectStatus().isBadRequest()
          .expectBody()
          .jsonPath("$.status").isEqualTo(400)
          .jsonPath("$.message").isEqualTo("Una o más tecnologías proporcionadas no existen en el sistema.");

        verify(mapper, times(1)).toDomain(any(CapacityRequest.class));
        verify(capacityServicePort, times(1)).saveCapacity(any(Capacity.class));
    }

    @Test
    @WithMockUser(roles = "PERSONA")
    void createCapacity_Returns403_WhenUserIsNotAdmin() {
        // Arrange
        CapacityRequest request = new CapacityRequest();
        request.setName("Backend Developer");
        request.setDescription("Capacidad para crear APIs");
        request.setTechnologyIds(Arrays.asList(1L, 2L, 3L));

        // Act & Assert
        webTestClient.post()
          .uri("/api/v1/capacities")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(request)
          .exchange()
          .expectStatus().isForbidden();

        verify(mapper, never()).toDomain(any());
        verify(capacityServicePort, never()).saveCapacity(any());
    }
}
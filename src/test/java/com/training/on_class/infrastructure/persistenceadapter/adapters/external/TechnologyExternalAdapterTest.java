package com.training.on_class.infrastructure.persistenceadapter.adapters.external;

import com.training.on_class.domain.exceptions.BusinessException;
import com.training.on_class.infrastructure.adapters.external.adapter.TechnologyExternalAdapter;
import com.training.on_class.infrastructure.entrypoints.rest.dto.response.SuccessResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TechnologyExternalAdapterTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private TechnologyExternalAdapter adapter;

    @BeforeEach
    void setUp() {
        RetryRegistry retryRegistry = RetryRegistry.ofDefaults();
        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();

        adapter = new TechnologyExternalAdapter(webClient, retryRegistry, circuitBreakerRegistry);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void mockWebClientChain() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void shouldReturnTrueWhenTechnologiesExist() {
        // Arrange
        List<Long> techIds = Arrays.asList(1L, 2L, 3L);
        SuccessResponse<Boolean> mockResponse = new SuccessResponse<>("Success", true);

        mockWebClientChain();
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
          .thenReturn(Mono.just(mockResponse));

        // Act
        Mono<Boolean> result = adapter.allTechnologiesExist(techIds);

        // Assert
        StepVerifier.create(result)
          .expectNext(true)
          .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenTechnologiesDoNotExist() {
        // Arrange
        List<Long> techIds = Arrays.asList(1L, 99L);
        SuccessResponse<Boolean> mockResponse = new SuccessResponse<>("Success", false);

        mockWebClientChain();
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
          .thenReturn(Mono.just(mockResponse));

        // Act
        Mono<Boolean> result = adapter.allTechnologiesExist(techIds);

        // Assert
        StepVerifier.create(result)
          .expectNext(false)
          .verifyComplete();
    }

    @Test
    void shouldThrowBusinessExceptionWhenWebClientThrowsHttpError() {
        // Arrange
        List<Long> techIds = Arrays.asList(1L, 2L);

        mockWebClientChain();

        WebClientResponseException mockHttpError = WebClientResponseException.create(
          500, "Internal Server Error", null, null, null);

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
          .thenReturn(Mono.error(mockHttpError));

        // Act
        Mono<Boolean> result = adapter.allTechnologiesExist(techIds);

        // Assert
        StepVerifier.create(result)
          .expectErrorMatches(throwable ->
            throwable instanceof BusinessException &&
              throwable.getMessage().equals("El servicio de validación de tecnologías no está disponible en este momento. Intente más tarde."))
          .verify();
    }

    @Test
    void shouldThrowBusinessExceptionWhenConnectionFails() {
        // Arrange
        List<Long> techIds = Arrays.asList(1L, 2L);

        mockWebClientChain();

        RuntimeException mockConnectionError = new RuntimeException("Connection refused");

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
          .thenReturn(Mono.error(mockConnectionError));

        // Act
        Mono<Boolean> result = adapter.allTechnologiesExist(techIds);

        // Assert
        StepVerifier.create(result)
          .expectErrorMatches(throwable ->
            throwable instanceof BusinessException &&
              throwable.getMessage().equals("El servicio de validación de tecnologías no está disponible en este momento. Intente más tarde."))
          .verify();
    }
}
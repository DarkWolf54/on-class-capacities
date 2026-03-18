package com.training.on_class.infrastructure.adapters.external.adapter;

import com.training.on_class.domain.exceptions.BusinessException;
import com.training.on_class.domain.ports.outbound.ITechnologyServicePort;
import com.training.on_class.infrastructure.entrypoints.rest.dto.response.SuccessResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TechnologyExternalAdapter implements ITechnologyServicePort {

    private final WebClient technologyWebClient;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public TechnologyExternalAdapter(WebClient technologyWebClient,
                                     RetryRegistry retryRegistry,
                                     CircuitBreakerRegistry circuitBreakerRegistry) {
        this.technologyWebClient = technologyWebClient;
        this.retry = retryRegistry.retry("technologyService");
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("technologyService");
    }

    @Override
    public Mono<Boolean> allTechnologiesExist(List<Long> technologyIds) {

        String idsParam = technologyIds.stream()
          .map(String::valueOf)
          .collect(Collectors.joining(","));

        log.info("Llamando al MS de Tecnologías para validar IDs: {}", idsParam);

        return technologyWebClient.get()
          .uri(uriBuilder -> uriBuilder
            .path("/api/v1/technologies/validate")
            .queryParam("ids", idsParam)
            .build())
          .retrieve()
          .bodyToMono(new ParameterizedTypeReference<SuccessResponse<Boolean>>() {})
          .map(SuccessResponse::getData)
          .transformDeferred(RetryOperator.of(retry))
          .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
          .onErrorResume(ex -> handleFailedResponse(idsParam, ex));
    }

    private Mono<Boolean> handleFailedResponse(String idsParam, Throwable ex) {
        if (ex instanceof WebClientResponseException webEx) {
            log.error("Error HTTP {} del MS de Tecnologías al validar IDs {}. Respuesta: {}",
              webEx.getStatusCode(), idsParam, webEx.getResponseBodyAsString(), ex);
        } else {
            log.error("Fallo de conexión o timeout con MS de Tecnologías al validar IDs {}. Causa: {}",
              idsParam, ex.getMessage(), ex);
        }

        return Mono.error(new BusinessException("El servicio de validación de tecnologías no está disponible en este momento. Intente más tarde."));
    }
}
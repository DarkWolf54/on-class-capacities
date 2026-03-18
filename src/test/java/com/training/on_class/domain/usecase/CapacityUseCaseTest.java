package com.training.on_class.domain.usecase;

import com.training.on_class.domain.exceptions.BusinessException;
import com.training.on_class.domain.model.Capacity;
import com.training.on_class.domain.ports.outbound.ICapacityPersistencePort;
import com.training.on_class.domain.ports.outbound.ITechnologyServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CapacityUseCaseTest {

    @Mock
    private ICapacityPersistencePort capacityPersistencePort;

    @Mock
    private ITechnologyServicePort technologyServicePort;

    @InjectMocks
    private CapacityUseCase capacityUseCase;

    @Test
    void shouldSaveCapacityWhenAllTechnologiesExist() {
        // Arrange
        Capacity validCapacity = new Capacity(null, "Backend", "Dev", Arrays.asList(1L, 2L, 3L));
        Capacity savedCapacity = new Capacity(1L, "Backend", "Dev", Arrays.asList(1L, 2L, 3L));

        when(technologyServicePort.allTechnologiesExist(validCapacity.getTechnologyIds()))
          .thenReturn(Mono.just(true));

        when(capacityPersistencePort.saveCapacity(any(Capacity.class)))
          .thenReturn(Mono.just(savedCapacity));

        // Act
        Mono<Capacity> result = capacityUseCase.saveCapacity(validCapacity);

        // Assert
        StepVerifier.create(result)
          .expectNext(savedCapacity)
          .verifyComplete();

        verify(technologyServicePort, times(1)).allTechnologiesExist(anyList());
        verify(capacityPersistencePort, times(1)).saveCapacity(any(Capacity.class));
    }

    @Test
    void shouldThrowExceptionWhenTechnologiesDoNotExist() {
        // Arrange
        Capacity validCapacity = new Capacity(null, "Backend", "Dev", Arrays.asList(1L, 2L, 3L));

        when(technologyServicePort.allTechnologiesExist(validCapacity.getTechnologyIds()))
          .thenReturn(Mono.just(false));

        // Act
        Mono<Capacity> result = capacityUseCase.saveCapacity(validCapacity);

        // Assert
        StepVerifier.create(result)
          .expectErrorMatches(throwable ->
            throwable instanceof BusinessException &&
              throwable.getMessage().equals("Una o más tecnologías proporcionadas no existen en el sistema."))
          .verify();

        verify(technologyServicePort, times(1)).allTechnologiesExist(anyList());
        verify(capacityPersistencePort, never()).saveCapacity(any(Capacity.class));
    }
}
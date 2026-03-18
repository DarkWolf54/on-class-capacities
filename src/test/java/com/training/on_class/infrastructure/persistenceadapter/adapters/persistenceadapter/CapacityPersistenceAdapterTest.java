package com.training.on_class.infrastructure.persistenceadapter.adapters.persistenceadapter;

import com.training.on_class.domain.model.Capacity;
import com.training.on_class.infrastructure.adapters.persistenceadapter.adapter.CapacityPersistenceAdapter;
import com.training.on_class.infrastructure.adapters.persistenceadapter.entities.CapacityEntity;
import com.training.on_class.infrastructure.adapters.persistenceadapter.entities.CapacityTechnologyEntity;
import com.training.on_class.infrastructure.adapters.persistenceadapter.mappers.ICapacityPersistenceMapper;
import com.training.on_class.infrastructure.adapters.persistenceadapter.repositories.ICapacityRepository;
import com.training.on_class.infrastructure.adapters.persistenceadapter.repositories.ICapacityTechnologyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CapacityPersistenceAdapterTest {

    @Mock
    private ICapacityRepository capacityRepository;

    @Mock
    private ICapacityTechnologyRepository capacityTechnologyRepository;

    @Mock
    private ICapacityPersistenceMapper mapper;

    @InjectMocks
    private CapacityPersistenceAdapter adapter;

    @Test
    void shouldSaveCapacityAndRelationsSuccessfully() {
        // Arrange
        List<Long> techIds = Arrays.asList(1L, 2L, 3L);

        Capacity domainCapacity = new Capacity(null, "Backend Developer", "Desc", techIds);
        CapacityEntity unsavedEntity = new CapacityEntity(null, "Backend Developer", "Desc");

        CapacityEntity savedEntity = new CapacityEntity(1L, "Backend Developer", "Desc");

        CapacityTechnologyEntity rel1 = new CapacityTechnologyEntity(100L, 1L, 1L);
        CapacityTechnologyEntity rel2 = new CapacityTechnologyEntity(101L, 1L, 2L);
        CapacityTechnologyEntity rel3 = new CapacityTechnologyEntity(102L, 1L, 3L);

        Capacity expectedFinalCapacity = new Capacity(1L, "Backend Developer", "Desc", techIds);

        when(mapper.toEntity(domainCapacity)).thenReturn(unsavedEntity);

        when(capacityRepository.save(unsavedEntity)).thenReturn(Mono.just(savedEntity));

        when(capacityTechnologyRepository.saveAll(anyIterable())).thenReturn(Flux.just(rel1, rel2, rel3));

        when(mapper.toDomain(savedEntity, techIds)).thenReturn(expectedFinalCapacity);

        // Act
        Mono<Capacity> result = adapter.saveCapacity(domainCapacity);

        // Assert
        StepVerifier.create(result)
          .expectNext(expectedFinalCapacity)
          .verifyComplete();

        verify(mapper, times(1)).toEntity(domainCapacity);
        verify(capacityRepository, times(1)).save(unsavedEntity);
        verify(capacityTechnologyRepository, times(1)).saveAll(anyIterable());
        verify(mapper, times(1)).toDomain(savedEntity, techIds);
    }

    @Test
    void shouldPropagateErrorWhenCapacityRepositoryFails() {
        // Arrange
        List<Long> techIds = Arrays.asList(1L, 2L, 3L);
        Capacity domainCapacity = new Capacity(null, "Backend Developer", "Desc", techIds);
        CapacityEntity unsavedEntity = new CapacityEntity(null, "Backend Developer", "Desc");

        when(mapper.toEntity(domainCapacity)).thenReturn(unsavedEntity);

        when(capacityRepository.save(unsavedEntity)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act
        Mono<Capacity> result = adapter.saveCapacity(domainCapacity);

        // Assert
        StepVerifier.create(result)
          .expectErrorMessage("Database error")
          .verify();

        verify(capacityTechnologyRepository, never()).saveAll(anyIterable());
        verify(mapper, never()).toDomain(any(), any());
    }
}

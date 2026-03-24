package com.training.on_class.domain.usecase;

import com.training.on_class.domain.exceptions.BusinessException;
import com.training.on_class.domain.model.Capacity;
import com.training.on_class.domain.model.PaginatedList;
import com.training.on_class.domain.model.Technology;
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
import java.util.List;

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

    @Test
    void shouldReturnPaginatedCapacitiesWithEnrichedTechnologies() {
        // Arrange
        int page = 0;
        int size = 10;
        String sortBy = "name";
        String sortDirection = "asc";

        Capacity dbCapacity1 = new Capacity(1L, "Backend", "Dev", Arrays.asList(1L, 2L, 3L));
        Capacity dbCapacity2 = new Capacity(2L, "Frontend", "UI", Arrays.asList(4L, 5L, 6L));

        PaginatedList<Capacity> dbPaginatedList = new PaginatedList<>(
          Arrays.asList(dbCapacity1, dbCapacity2), page, size, 2, 1
        );

        Technology tech1 = new Technology(1L, "Java");
        Technology tech2 = new Technology(2L, "Spring");
        Technology tech3 = new Technology(3L, "PostgreSQL");
        Technology tech4 = new Technology(4L, "Angular");
        Technology tech5 = new Technology(5L, "TypeScript");
        Technology tech6 = new Technology(6L, "RxJS");
        List<Technology> externalTechnologies = Arrays.asList(tech1, tech2, tech3, tech4, tech5, tech6);

        when(capacityPersistencePort.findAllPaginated(page, size, sortBy, sortDirection))
          .thenReturn(Mono.just(dbPaginatedList));

        when(technologyServicePort.getTechnologiesByIds(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L)))
          .thenReturn(Mono.just(externalTechnologies));

        // Act
        Mono<PaginatedList<Capacity>> result = capacityUseCase.getAllCapacities(page, size, sortBy, sortDirection);

        // Assert
        StepVerifier.create(result)
          .assertNext(paginatedList -> {
              assert paginatedList.getTotalElements() == 2;
              assert paginatedList.getData().size() == 2;

              Capacity firstCapacity = paginatedList.getData().getFirst();
              assert firstCapacity.getName().equals("Backend");
              assert firstCapacity.getTechnologies().size() == 3;
              assert firstCapacity.getTechnologies().getFirst().getName().equals("Java");

              Capacity secondCapacity = paginatedList.getData().get(1);
              assert secondCapacity.getName().equals("Frontend");
              assert secondCapacity.getTechnologies().size() == 3;
              assert secondCapacity.getTechnologies().getFirst().getName().equals("Angular");
          })
          .verifyComplete();

        verify(capacityPersistencePort, times(1)).findAllPaginated(page, size, sortBy, sortDirection);
        verify(technologyServicePort, times(1)).getTechnologiesByIds(anyList());
    }

    @Test
    void shouldReturnEmptyPaginatedListWithoutCallingTechnologyService() {
        int page = 0;
        int size = 10;
        String sortBy = "name";
        String sortDirection = "asc";

        PaginatedList<Capacity> emptyDbList = new PaginatedList<>(List.of(), page, size, 0, 0);

        when(capacityPersistencePort.findAllPaginated(page, size, sortBy, sortDirection))
          .thenReturn(Mono.just(emptyDbList));

        // Act
        Mono<PaginatedList<Capacity>> result = capacityUseCase.getAllCapacities(page, size, sortBy, sortDirection);

        // Assert
        StepVerifier.create(result)
          .assertNext(paginatedList -> {
              assert paginatedList.getData().isEmpty();
              assert paginatedList.getTotalElements() == 0;
          })
          .verifyComplete();

        verify(capacityPersistencePort, times(1)).findAllPaginated(page, size, sortBy, sortDirection);
        verify(technologyServicePort, never()).getTechnologiesByIds(anyList());
    }
}
package com.training.on_class.infrastructure.adapters.persistenceadapter.adapter;

import com.training.on_class.domain.model.Capacity;
import com.training.on_class.domain.model.PaginatedList;
import com.training.on_class.domain.ports.outbound.ICapacityPersistencePort;
import com.training.on_class.infrastructure.adapters.persistenceadapter.entities.CapacityEntity;
import com.training.on_class.infrastructure.adapters.persistenceadapter.entities.CapacityTechnologyEntity;
import com.training.on_class.infrastructure.adapters.persistenceadapter.mappers.ICapacityPersistenceMapper;
import com.training.on_class.infrastructure.adapters.persistenceadapter.repositories.ICapacityRepository;
import com.training.on_class.infrastructure.adapters.persistenceadapter.repositories.ICapacityTechnologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CapacityPersistenceAdapter implements ICapacityPersistencePort {

    private final ICapacityRepository capacityRepository;
    private final ICapacityTechnologyRepository capacityTechnologyRepository;
    private final ICapacityPersistenceMapper mapper;

    @Override
    public Mono<Capacity> saveCapacity(Capacity capacity) {
        CapacityEntity capacityEntity = mapper.toEntity(capacity);

        return capacityRepository.save(capacityEntity)
          .flatMap(savedCapacity -> {
              List<CapacityTechnologyEntity> relations = capacity.getTechnologyIds().stream()
                .map(techId -> new CapacityTechnologyEntity(null, savedCapacity.getId(), techId))
                .toList();
              return capacityTechnologyRepository.saveAll(relations)
                .collectList()
                .thenReturn(mapper.toDomain(savedCapacity, capacity.getTechnologyIds()));
          });
    }

    @Override
    public Mono<PaginatedList<Capacity>> findAllPaginated(int page, int size, String sortBy, String sortDirection) {

        String dbSortColumn = "technologyCount".equalsIgnoreCase(sortBy) ? "tech_count" : "c.name";
        String dbSortDirection = "desc".equalsIgnoreCase(sortDirection) ? "DESC" : "ASC";
        int offset = page * size;

        Mono<Long> totalElementsMono = capacityRepository.countAllCapacities().defaultIfEmpty(0L);

        Mono<List<Capacity>> dataMono = capacityRepository
          .findCapacitiesPaginatedCustom(size, offset, dbSortColumn, dbSortDirection)
          .map(projections -> projections.stream()
            .map(p -> new Capacity(p.id(), p.name(), p.description(), p.techIds()))
            .toList()
          );

        return Mono.zip(dataMono, totalElementsMono)
          .map(tuple -> {
              List<Capacity> capacities = tuple.getT1();
              Long totalElements = tuple.getT2();
              int totalPages = (int) Math.ceil((double) totalElements / size);
              return new PaginatedList<>(capacities, page, size, totalElements, totalPages);
          });
    }
}
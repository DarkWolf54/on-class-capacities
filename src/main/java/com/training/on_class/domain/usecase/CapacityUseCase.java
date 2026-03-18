package com.training.on_class.domain.usecase;

import com.training.on_class.domain.exceptions.BusinessException;
import com.training.on_class.domain.model.Capacity;
import com.training.on_class.domain.model.PaginatedList;
import com.training.on_class.domain.model.Technology;
import com.training.on_class.domain.ports.inbound.ICapacityServicePort;
import com.training.on_class.domain.ports.outbound.ICapacityPersistencePort;
import com.training.on_class.domain.ports.outbound.ITechnologyServicePort;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CapacityUseCase implements ICapacityServicePort {

    private final ICapacityPersistencePort capacityPersistencePort;
    private final ITechnologyServicePort technologyServicePort;

    public CapacityUseCase(ICapacityPersistencePort capacityPersistencePort, ITechnologyServicePort technologyServicePort) {
        this.capacityPersistencePort = capacityPersistencePort;
        this.technologyServicePort = technologyServicePort;
    }

    @Override
    public Mono<Capacity> saveCapacity(Capacity capacity) {
        return technologyServicePort.allTechnologiesExist(capacity.getTechnologyIds())
          .flatMap(allExist -> {
              if (Boolean.FALSE.equals(allExist)) {
                  return Mono.error(new BusinessException("Una o más tecnologías proporcionadas no existen en el sistema."));
              }
              return capacityPersistencePort.saveCapacity(capacity);
          });
    }

    @Override
    public Mono<PaginatedList<Capacity>> getAllCapacities(int page, int size, String sortBy, String sortDirection) {
        return capacityPersistencePort.findAllPaginated(page, size, sortBy, sortDirection)
          .flatMap(paginatedList -> {
              List<Capacity> capacities = paginatedList.getData();

              if (capacities.isEmpty()) {
                  return Mono.just(paginatedList);
              }

              List<Long> allTechIdsOnPage = capacities.stream()
                .flatMap(capacity -> capacity.getTechnologyIds().stream())
                .distinct()
                .toList();

              return technologyServicePort.getTechnologiesByIds(allTechIdsOnPage)
                .map(technologies -> {
                    Map<Long, Technology> techMap = technologies.stream()
                      .collect(Collectors.toMap(Technology::getId, tech -> tech));

                    List<Capacity> enrichedCapacities = capacities.stream().map(capacity -> {
                        List<Technology> fullTechnologies = capacity.getTechnologyIds().stream()
                          .map(techMap::get)
                          .filter(Objects::nonNull)
                          .toList();

                        return new Capacity(
                          capacity.getId(),
                          capacity.getName(),
                          capacity.getDescription(),
                          fullTechnologies,
                          true
                        );
                    }).toList();

                    return new PaginatedList<>(
                      enrichedCapacities,
                      paginatedList.getPage(),
                      paginatedList.getSize(),
                      paginatedList.getTotalElements(),
                      paginatedList.getTotalPages()
                    );
                });
          });
    }
}

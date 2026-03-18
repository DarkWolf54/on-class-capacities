package com.training.on_class.domain.usecase;

import com.training.on_class.domain.exceptions.BusinessException;
import com.training.on_class.domain.model.Capacity;
import com.training.on_class.domain.ports.inbound.ICapacityServicePort;
import com.training.on_class.domain.ports.outbound.ICapacityPersistencePort;
import com.training.on_class.domain.ports.outbound.ITechnologyServicePort;
import reactor.core.publisher.Mono;

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
}

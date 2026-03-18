package com.training.on_class.infrastructure.adapters.persistenceadapter.adapter;

import com.training.on_class.domain.model.Capacity;
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
    private final ICapacityPersistenceMapper mapper; // <--- Inyectamos el mapper

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
                .map(savedRelations -> mapper.toDomain(savedCapacity, capacity.getTechnologyIds()));
          });
    }
}

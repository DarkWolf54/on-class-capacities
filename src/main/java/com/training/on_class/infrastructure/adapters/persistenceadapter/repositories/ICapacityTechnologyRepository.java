package com.training.on_class.infrastructure.adapters.persistenceadapter.repositories;

import com.training.on_class.infrastructure.adapters.persistenceadapter.entities.CapacityTechnologyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ICapacityTechnologyRepository extends ReactiveCrudRepository<CapacityTechnologyEntity, Long> {
}

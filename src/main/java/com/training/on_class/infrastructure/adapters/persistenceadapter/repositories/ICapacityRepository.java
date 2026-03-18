package com.training.on_class.infrastructure.adapters.persistenceadapter.repositories;

import com.training.on_class.infrastructure.adapters.persistenceadapter.entities.CapacityEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ICapacityRepository extends ReactiveCrudRepository<CapacityEntity, Long> {
}

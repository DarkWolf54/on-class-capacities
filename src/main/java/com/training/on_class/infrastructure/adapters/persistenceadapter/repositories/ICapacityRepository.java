package com.training.on_class.infrastructure.adapters.persistenceadapter.repositories;

import com.training.on_class.infrastructure.adapters.persistenceadapter.entities.CapacityEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ICapacityRepository extends ReactiveCrudRepository<CapacityEntity, Long>, ICapacityCustomRepository {
    @Query("SELECT COUNT(id) FROM capacity")
    Mono<Long> countAllCapacities();
}

package com.training.on_class.infrastructure.adapters.persistenceadapter.repositories;

import com.training.on_class.infrastructure.adapters.persistenceadapter.entities.CapacityProjection;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ICapacityCustomRepository {
    Mono<List<CapacityProjection>> findCapacitiesPaginatedCustom(
      int limit, int offset, String dbSortColumn, String dbSortDirection
    );
}

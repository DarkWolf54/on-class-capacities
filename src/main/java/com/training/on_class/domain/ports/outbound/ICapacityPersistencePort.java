package com.training.on_class.domain.ports.outbound;

import com.training.on_class.domain.model.Capacity;
import com.training.on_class.domain.model.PaginatedList;
import reactor.core.publisher.Mono;

public interface ICapacityPersistencePort {
    Mono<Capacity> saveCapacity(Capacity capacity);
    Mono<PaginatedList<Capacity>> findAllPaginated(int page, int size, String sortBy, String sortDirection);
}

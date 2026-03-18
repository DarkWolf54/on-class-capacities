package com.training.on_class.domain.ports.inbound;

import com.training.on_class.domain.model.Capacity;
import com.training.on_class.domain.model.PaginatedList;
import reactor.core.publisher.Mono;

public interface ICapacityServicePort {
    Mono<Capacity> saveCapacity(Capacity capacity);
    Mono<PaginatedList<Capacity>> getAllCapacities(int page, int size, String sortBy, String sortDirection);
}

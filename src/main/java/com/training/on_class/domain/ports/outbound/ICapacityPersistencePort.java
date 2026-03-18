package com.training.on_class.domain.ports.outbound;

import com.training.on_class.domain.model.Capacity;
import reactor.core.publisher.Mono;

public interface ICapacityPersistencePort {
    Mono<Capacity> saveCapacity(Capacity capacity);
}

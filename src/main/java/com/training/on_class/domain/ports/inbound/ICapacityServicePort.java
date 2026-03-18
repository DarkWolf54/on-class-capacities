package com.training.on_class.domain.ports.inbound;

import com.training.on_class.domain.model.Capacity;
import reactor.core.publisher.Mono;

public interface ICapacityServicePort {
    Mono<Capacity> saveCapacity(Capacity capacity);
}

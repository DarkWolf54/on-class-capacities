package com.training.on_class.domain.ports.outbound;

import reactor.core.publisher.Mono;
import java.util.List;

public interface ITechnologyServicePort {
    Mono<Boolean> allTechnologiesExist(List<Long> technologyIds);
}

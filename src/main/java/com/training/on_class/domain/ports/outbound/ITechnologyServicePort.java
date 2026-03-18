package com.training.on_class.domain.ports.outbound;

import com.training.on_class.domain.model.Technology;
import reactor.core.publisher.Mono;
import java.util.List;

public interface ITechnologyServicePort {
    Mono<Boolean> allTechnologiesExist(List<Long> technologyIds);
    Mono<List<Technology>> getTechnologiesByIds(List<Long> technologyIds);
}

package com.training.on_class.infrastructure.adapters.persistenceadapter.entities;

import java.util.List;

public record CapacityProjection(
  Long id,
  String name,
  String description,
  Long techCount,
  List<Long> techIds
) {
}
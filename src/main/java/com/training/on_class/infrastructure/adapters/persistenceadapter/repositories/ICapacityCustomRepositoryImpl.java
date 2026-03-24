package com.training.on_class.infrastructure.adapters.persistenceadapter.repositories;

import com.training.on_class.infrastructure.adapters.persistenceadapter.entities.CapacityProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class ICapacityCustomRepositoryImpl implements ICapacityCustomRepository {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<List<CapacityProjection>> findCapacitiesPaginatedCustom(
      int limit, int offset, String dbSortColumn, String dbSortDirection) {

        String query = buildSafeQuery(dbSortColumn, dbSortDirection);

        return databaseClient.sql(query)
          .bind("limit", limit)
          .bind("offset", offset)
          .map(row -> {
              Long id = row.get("id", Long.class);
              String name = row.get("name", String.class);
              String description = row.get("description", String.class);
              Long techCount = row.get("tech_count", Long.class);
              Long[] techIdsArray = row.get("tech_ids", Long[].class);

              List<Long> techIds = techIdsArray != null ? Arrays.asList(techIdsArray) : List.of();

              return new CapacityProjection(id, name, description, techCount, techIds);
          })
          .all()
          .collectList();
    }

    private String buildSafeQuery(String sortColumn, String sortDirection) {
        String baseQuery = "SELECT c.id, c.name, c.description, COUNT(ct.technology_id) as tech_count, " +
          "ARRAY_AGG(ct.technology_id) as tech_ids " +
          "FROM capacity c " +
          "LEFT JOIN capacity_technology ct ON c.id = ct.capacity_id " +
          "GROUP BY c.id, c.name, c.description ";

        boolean isTechCount = "tech_count".equals(sortColumn);
        boolean isDesc = "DESC".equals(sortDirection);

        if (isTechCount) {
            return isDesc
              ? baseQuery + "ORDER BY tech_count DESC LIMIT :limit OFFSET :offset"
              : baseQuery + "ORDER BY tech_count ASC LIMIT :limit OFFSET :offset";
        } else {
            return isDesc
              ? baseQuery + "ORDER BY c.name DESC LIMIT :limit OFFSET :offset"
              : baseQuery + "ORDER BY c.name ASC LIMIT :limit OFFSET :offset";
        }
    }
}
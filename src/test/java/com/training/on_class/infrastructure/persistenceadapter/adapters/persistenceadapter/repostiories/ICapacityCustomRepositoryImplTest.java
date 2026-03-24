package com.training.on_class.infrastructure.persistenceadapter.adapters.persistenceadapter.repostiories;

import com.training.on_class.infrastructure.adapters.persistenceadapter.entities.CapacityProjection;
import com.training.on_class.infrastructure.adapters.persistenceadapter.repositories.ICapacityCustomRepositoryImpl;
import io.r2dbc.spi.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ICapacityCustomRepositoryImplTest {

    @Mock
    private DatabaseClient databaseClient;

    @Mock
    private DatabaseClient.GenericExecuteSpec genericExecuteSpec;

    @Mock
    private RowsFetchSpec<CapacityProjection> rowsFetchSpec;

    @InjectMocks
    private ICapacityCustomRepositoryImpl customRepository;

    @Captor
    private ArgumentCaptor<String> sqlCaptor;

    @BeforeEach
    void setUp() {
        when(databaseClient.sql(anyString())).thenReturn(genericExecuteSpec);
        when(genericExecuteSpec.bind(anyString(), any())).thenReturn(genericExecuteSpec);
        @SuppressWarnings("unchecked")
        RowsFetchSpec<CapacityProjection> mockFetchSpec = (RowsFetchSpec<CapacityProjection>) mock(RowsFetchSpec.class);
        this.rowsFetchSpec = mockFetchSpec;
        when(genericExecuteSpec.map(any(Function.class))).thenReturn(rowsFetchSpec);
    }

    @Test
    void shouldExecuteQueryOrderedByNameAscending() {
        // Arrange
        int limit = 10;
        int offset = 0;

        CapacityProjection projection = new CapacityProjection(1L, "Java", "Desc", 3L, List.of(1L, 2L, 3L));
        when(rowsFetchSpec.all()).thenReturn(Flux.just(projection));

        // Act
        Mono<List<CapacityProjection>> result = customRepository.findCapacitiesPaginatedCustom(limit, offset, "c.name", "ASC");

        // Assert
        StepVerifier.create(result)
          .expectNextMatches(list -> list.size() == 1 && list.get(0).name().equals("Java"))
          .verifyComplete();

        verify(genericExecuteSpec).bind("limit", limit);
        verify(genericExecuteSpec).bind("offset", offset);

        verify(databaseClient).sql(sqlCaptor.capture());
        String generatedSql = sqlCaptor.getValue();

        assertThat(generatedSql).contains("ORDER BY c.name ASC LIMIT :limit OFFSET :offset");
    }

    @Test
    void shouldExecuteQueryOrderedByTechnologyCountDescending() {
        // Arrange
        when(rowsFetchSpec.all()).thenReturn(Flux.empty());

        // Act
        Mono<List<CapacityProjection>> result = customRepository.findCapacitiesPaginatedCustom(5, 10, "tech_count", "DESC");

        // Assert
        StepVerifier.create(result)
          .expectNextMatches(List::isEmpty)
          .verifyComplete();

        verify(databaseClient).sql(sqlCaptor.capture());
        String generatedSql = sqlCaptor.getValue();

        assertThat(generatedSql).contains("ORDER BY tech_count DESC LIMIT :limit OFFSET :offset");
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void shouldMapRowCorrectlyToProjection() {
        // Arrange
        Row mockRow = mock(Row.class);
        when(mockRow.get("id", Long.class)).thenReturn(99L);
        when(mockRow.get("name", String.class)).thenReturn("DevOps");
        when(mockRow.get("description", String.class)).thenReturn("Infra");
        when(mockRow.get("tech_count", Long.class)).thenReturn(2L);
        when(mockRow.get("tech_ids", Long[].class)).thenReturn(new Long[]{10L, 20L});

        ArgumentCaptor<Function> mapperCaptor = ArgumentCaptor.forClass(Function.class);
        when(rowsFetchSpec.all()).thenReturn(Flux.empty());

        // Act
        customRepository.findCapacitiesPaginatedCustom(10, 0, "c.name", "ASC");

        // Assert
        verify(genericExecuteSpec).map(mapperCaptor.capture());

        Function<Row, CapacityProjection> rowMapper = mapperCaptor.getValue();

        CapacityProjection mappedProjection = rowMapper.apply(mockRow);

        assertThat(mappedProjection.id()).isEqualTo(99L);
        assertThat(mappedProjection.name()).isEqualTo("DevOps");
        assertThat(mappedProjection.techCount()).isEqualTo(2L);
        assertThat(mappedProjection.techIds()).containsExactly(10L, 20L);
    }
}
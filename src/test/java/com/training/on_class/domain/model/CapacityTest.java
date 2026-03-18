package com.training.on_class.domain.model;

import com.training.on_class.domain.exceptions.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class CapacityTest {

    @Test
    void shouldCreateCapacityWhenValidDataIsProvided() {
        // Arrange
        List<Long> validTechs = Arrays.asList(1L, 2L, 3L, 4L);

        // Act
        Capacity capacity = new Capacity(1L, "Backend", "Desarrollo Backend", validTechs);

        // Assert
        assertNotNull(capacity);
        assertEquals("Backend", capacity.getName());
        assertEquals(4, capacity.getTechnologyIds().size());
    }

    @Test
    void shouldThrowExceptionWhenTechnologyIdsAreLessThanThree() {
        // Arrange
        List<Long> invalidTechs = Arrays.asList(1L, 2L);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            new Capacity(1L, "Backend", "Desarrollo Backend", invalidTechs);
        });

        assertEquals("Una capacidad debe tener al menos 3 tecnologías.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTechnologyIdsAreMoreThanTwenty() {
        // Arrange
        List<Long> invalidTechs = IntStream.rangeClosed(1, 21)
          .mapToObj(Long::valueOf)
          .toList();

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            new Capacity(1L, "Backend", "Desarrollo Backend", invalidTechs);
        });

        assertEquals("Una capacidad no puede tener más de 20 tecnologías.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTechnologyIdsAreDuplicated() {
        // Arrange
        List<Long> duplicatedTechs = Arrays.asList(1L, 2L, 2L, 3L);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            new Capacity(1L, "Backend", "Desarrollo Backend", duplicatedTechs);
        });

        assertEquals("La capacidad no puede tener tecnologías duplicadas.", exception.getMessage());
    }
}
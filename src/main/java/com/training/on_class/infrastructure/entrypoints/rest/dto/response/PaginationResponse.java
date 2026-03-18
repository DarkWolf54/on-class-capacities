package com.training.on_class.infrastructure.entrypoints.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PaginationResponse<T> {
    private List<T> data;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}

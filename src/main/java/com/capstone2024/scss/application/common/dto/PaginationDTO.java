package com.capstone2024.scss.application.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaginationDTO<T> {
    private T data;
    private Integer totalPages;
    private Integer totalElements;
}


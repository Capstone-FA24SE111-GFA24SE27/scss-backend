package com.capstone2024.scss.application.event.category.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryDTO {
    private Long id;
    private String code;
    private String name;
}

package com.capstone2024.scss.domain.common.mapper.event;

import com.capstone2024.scss.application.event.category.dto.CategoryDTO;
import com.capstone2024.scss.domain.event.entities.Category;

public class CategoryMapper {
    public static CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryDTO.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .build();
    }
}

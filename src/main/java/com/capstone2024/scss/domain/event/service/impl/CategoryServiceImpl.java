package com.capstone2024.scss.domain.event.service.impl;

import com.capstone2024.scss.application.event.category.dto.CategoryDTO;
import com.capstone2024.scss.domain.event.entities.Category;
import com.capstone2024.scss.domain.event.service.CategoryService;
import com.capstone2024.scss.infrastructure.repositories.event.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> CategoryDTO.builder()
                        .id(category.getId())
                        .code(category.getCode())
                        .name(category.getName())
                        .build())
                .collect(Collectors.toList());
    }
}

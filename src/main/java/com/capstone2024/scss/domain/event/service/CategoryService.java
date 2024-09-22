package com.capstone2024.scss.domain.event.service;

import com.capstone2024.scss.application.event.category.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
}

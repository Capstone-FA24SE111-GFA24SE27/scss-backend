package com.capstone2024.scss.application.event.category.controller;

import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.event.category.dto.CategoryDTO;
import com.capstone2024.scss.domain.event.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Object> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseUtil.getResponse(categories, HttpStatus.OK);
    }
}

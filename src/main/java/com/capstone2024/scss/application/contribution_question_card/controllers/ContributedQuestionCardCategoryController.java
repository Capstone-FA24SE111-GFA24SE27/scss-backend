package com.capstone2024.scss.application.contribution_question_card.controllers;

import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.contribution_question_card.dto.ContributedQuestionCardCategoryDTO;
import com.capstone2024.scss.domain.contribution_question_card.services.ContributedQuestionCardCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/contribution-question-cards/categories")
@RequiredArgsConstructor
public class ContributedQuestionCardCategoryController {

    private final ContributedQuestionCardCategoryService categoryService;

    @PostMapping
    public ResponseEntity<Object> createCategory(@RequestBody ContributedQuestionCardCategoryDTO categoryDTO) {
        ContributedQuestionCardCategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return ResponseUtil.getResponse(createdCategory, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCategoryById(@PathVariable Long id) {
        ContributedQuestionCardCategoryDTO categoryDTO = categoryService.getCategoryById(id);
        return ResponseUtil.getResponse(categoryDTO, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> getAllCategories() {
        List<ContributedQuestionCardCategoryDTO> categories = categoryService.getAllCategories();
        return ResponseUtil.getResponse(categories, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCategory(@PathVariable Long id, @RequestBody ContributedQuestionCardCategoryDTO categoryDTO) {
        ContributedQuestionCardCategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseUtil.getResponse(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseUtil.getResponse("Delete successfully", HttpStatus.OK);
    }
}
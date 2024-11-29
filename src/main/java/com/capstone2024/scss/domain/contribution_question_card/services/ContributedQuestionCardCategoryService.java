package com.capstone2024.scss.domain.contribution_question_card.services;

import com.capstone2024.scss.application.contribution_question_card.dto.ContributedQuestionCardCategoryDTO;

import java.util.List;

public interface ContributedQuestionCardCategoryService {
    ContributedQuestionCardCategoryDTO createCategory(ContributedQuestionCardCategoryDTO categoryDTO);
    ContributedQuestionCardCategoryDTO getCategoryById(Long id);
    List<ContributedQuestionCardCategoryDTO> getAllCategories();
    ContributedQuestionCardCategoryDTO updateCategory(Long id, ContributedQuestionCardCategoryDTO categoryDTO);
    void deleteCategory(Long id);
}

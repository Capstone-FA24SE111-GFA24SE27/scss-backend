package com.capstone2024.scss.domain.contribution_question_card.services.impl;

import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.contribution_question_card.dto.ContributedQuestionCardCategoryDTO;
import com.capstone2024.scss.domain.common.mapper.contribution_question_card.ContributedQuestionCardCategoryMapper;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributedQuestionCardCategory;
import com.capstone2024.scss.domain.contribution_question_card.services.ContributedQuestionCardCategoryService;
import com.capstone2024.scss.infrastructure.repositories.contribution_question_card.ContributedQuestionCardCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContributedQuestionCardCategoryServiceImpl implements ContributedQuestionCardCategoryService {

    private final ContributedQuestionCardCategoryRepository categoryRepository;

    @Override
    public ContributedQuestionCardCategoryDTO createCategory(ContributedQuestionCardCategoryDTO categoryDTO) {
        ContributedQuestionCardCategory category = ContributedQuestionCardCategoryMapper.toEntity(categoryDTO);
        category = categoryRepository.save(category);
        return ContributedQuestionCardCategoryMapper.toDTO(category);
    }

    @Override
    public ContributedQuestionCardCategoryDTO getCategoryById(Long id) {
        ContributedQuestionCardCategory category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
        return ContributedQuestionCardCategoryMapper.toDTO(category);
    }

    @Override
    public List<ContributedQuestionCardCategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(ContributedQuestionCardCategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ContributedQuestionCardCategoryDTO updateCategory(Long id, ContributedQuestionCardCategoryDTO categoryDTO) {
        ContributedQuestionCardCategory existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setType(categoryDTO.getType());
        categoryRepository.save(existingCategory);
        return ContributedQuestionCardCategoryMapper.toDTO(existingCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        ContributedQuestionCardCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        categoryRepository.delete(category);
    }
}

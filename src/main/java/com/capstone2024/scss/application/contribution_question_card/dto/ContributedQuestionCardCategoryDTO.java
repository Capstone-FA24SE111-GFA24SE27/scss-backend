package com.capstone2024.scss.application.contribution_question_card.dto;

import com.capstone2024.scss.domain.contribution_question_card.entities.ContributedQuestionCardCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContributedQuestionCardCategoryDTO {
    private Long id;
    private String name;
    private ContributedQuestionCardCategory.Type type;
}
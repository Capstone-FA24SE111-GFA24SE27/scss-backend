package com.capstone2024.scss.domain.common.mapper.contribution_question_card;

import com.capstone2024.scss.application.contribution_question_card.dto.ContributedQuestionCardCategoryDTO;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributedQuestionCardCategory;

public class ContributedQuestionCardCategoryMapper {

    // Map Entity to DTO
    public static ContributedQuestionCardCategoryDTO toDTO(ContributedQuestionCardCategory entity) {
        if (entity == null) {
            return null;
        }
        return new ContributedQuestionCardCategoryDTO(
                entity.getId(),
                entity.getName(),
                entity.getType()
        );
    }

    // Map DTO to Entity
    public static ContributedQuestionCardCategory toEntity(ContributedQuestionCardCategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        ContributedQuestionCardCategory entity = new ContributedQuestionCardCategory();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        return entity;
    }
}

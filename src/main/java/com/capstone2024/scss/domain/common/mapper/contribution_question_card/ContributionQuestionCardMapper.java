package com.capstone2024.scss.domain.common.mapper.contribution_question_card;

import com.capstone2024.scss.application.contribution_question_card.dto.ContributionQuestionCardDTO;
import com.capstone2024.scss.application.contribution_question_card.dto.ContributionQuestionCardResponseDTO;
import com.capstone2024.scss.domain.common.mapper.account.CounselorProfileMapper;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributionQuestionCard;
import com.capstone2024.scss.infrastructure.elastic_search.documents.ContributionQuestionCardDocument;

public class ContributionQuestionCardMapper {

    // Map Entity to ResponseDTO
    public static ContributionQuestionCardResponseDTO toResponseDTO(ContributionQuestionCard entity) {
        if (entity == null) {
            return null;
        }

        return ContributionQuestionCardResponseDTO.builder()
                .id(entity.getId().toString())
                .question(entity.getQuestion())
                .answer(entity.getAnswer())
                .status(entity.getPublicStatus().name())
                .counselor(CounselorProfileMapper.toCounselorProfileDTO(entity.getCounselor()))
                .category(ContributedQuestionCardCategoryMapper.toDTO(entity.getCategory()))
                .title(entity.getTitle())
                .build();
    }

    // Map Document to ResponseDTO
//    public static ContributionQuestionCardResponseDTO toResponseDTO(ContributionQuestionCardDocument document) {
//        if (document == null) {
//            return null;
//        }
//
//        return ContributionQuestionCardResponseDTO.builder()
//                .id(document.getId())
//                .question(document.getQuestion())
//                .answer(document.getAnswer())
//                .status(document.getStatus())
//                .counselor(CounselorProfileMapper.toCounselorProfileDTO(entity.getCounselor()))
//                .category(ContributedQuestionCardCategoryMapper.toDTO(entity.getCategory()))
//                .build();
//    }

    // Map DTO to Entity
    public static ContributionQuestionCard toEntity(ContributionQuestionCardDTO dto) {
        if (dto == null) {
            return null;
        }

        ContributionQuestionCard entity = new ContributionQuestionCard();
        entity.setQuestion(dto.getQuestion());
        entity.setAnswer(dto.getAnswer());
        return entity;
    }

    // Map Entity to Document
    public static ContributionQuestionCardDocument toDocument(ContributionQuestionCard entity) {
        if (entity == null) {
            return null;
        }

        ContributionQuestionCardDocument document = new ContributionQuestionCardDocument();
        document.setId(entity.getId().toString());
        document.setSortingId(entity.getId());
        document.setQuestion(entity.getQuestion());
        document.setAnswer(entity.getAnswer());
        document.setStatus(entity.getPublicStatus().name());
        document.setCounselorId(entity.getCounselor().getId().toString());
        document.setCategoryId(entity.getCategory().getId());
        return document;
    }
}

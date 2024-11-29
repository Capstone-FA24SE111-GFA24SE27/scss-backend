package com.capstone2024.scss.domain.contribution_question_card.services;

import com.capstone2024.scss.application.contribution_question_card.dto.ContributionQuestionCardResponseDTO;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributionQuestionCard;
import com.capstone2024.scss.infrastructure.elastic_search.documents.ContributionQuestionCardDocument;

import java.util.List;

public interface ContributionQuestionCardService {

    ContributionQuestionCard createContributionQuestionCard(String question, String answer, Long categoryId, Long counselorId);

    ContributionQuestionCard updateContributionQuestionCard(Long id, String question, String answer, Long categoryId, ContributionQuestionCard.Status status, Long counselorId);

    void deleteContributionQuestionCard(Long id);

    List<ContributionQuestionCardResponseDTO> searchContributionQuestionCards(String query, String status, Long counselorId, Long categoryId);
}
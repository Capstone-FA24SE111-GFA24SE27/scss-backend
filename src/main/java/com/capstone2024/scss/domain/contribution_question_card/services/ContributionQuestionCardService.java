package com.capstone2024.scss.domain.contribution_question_card.services;

import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.contribution_question_card.dto.ContributionQuestionCardResponseDTO;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributionQuestionCard;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContributionQuestionCardService {

    ContributionQuestionCard createContributionQuestionCard(String question, String answer, Long categoryId, Long counselorId, String title);

    ContributionQuestionCard updateContributionQuestionCard(Long id, String question, String answer, Long categoryId, ContributionQuestionCard.PublicStatus status, Long counselorId, String title);

    void deleteContributionQuestionCard(Long id);

    PaginationDTO<List<ContributionQuestionCardResponseDTO>> searchContributionQuestionCards(String query, String status, Long counselorId, Long categoryId, Pageable pageable, boolean isSuggestion);

    ContributionQuestionCardResponseDTO getOne(Long id);
}
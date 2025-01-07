package com.capstone2024.scss.domain.contribution_question_card.services;

import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.contribution_question_card.dto.ContributionQuestionCardDTO;
import com.capstone2024.scss.application.contribution_question_card.dto.ContributionQuestionCardResponseDTO;
import com.capstone2024.scss.application.q_and_a.dto.QuestionCardFilterRequestDTO;
import com.capstone2024.scss.application.q_and_a.dto.QuestionCardResponseDTO;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributionQuestionCard;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionCard;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ManageContributionQuestionCardService {
    PaginationDTO<List<ContributionQuestionCardResponseDTO>> searchContributionQuestionCards(String query, ContributionQuestionCard.PublicStatus status, Long counselorId, Long categoryId, Pageable pageable);

    void updatePublicStatusContributionQuestionCard(Long questionCardId, ContributionQuestionCard.PublicStatus questionCardPublicStatus);
}

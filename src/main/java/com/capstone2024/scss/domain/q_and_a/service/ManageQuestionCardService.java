package com.capstone2024.scss.domain.q_and_a.service;

import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.q_and_a.dto.QuestionCardFilterRequestDTO;
import com.capstone2024.scss.application.q_and_a.dto.QuestionCardResponseDTO;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionCard;

import java.util.List;

public interface ManageQuestionCardService {
    PaginationDTO<List<QuestionCardResponseDTO>> getPublicQuestionCardsForManage(QuestionCardFilterRequestDTO filterRequest);

    void updatePublicStatusQuestionCard(Long questionCardId, QuestionCard.PublicStatus questionCardPublicStatus);
}

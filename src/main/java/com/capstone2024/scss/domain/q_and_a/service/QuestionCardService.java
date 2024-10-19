package com.capstone2024.scss.domain.q_and_a.service;

import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.q_and_a.dto.*;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionCardStatus;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionType;

import java.util.List;

public interface QuestionCardService {
    QuestionCardResponseDTO createQuestionCard(CreateQuestionCardRequestDTO dto, Long studentId);
    PaginationDTO<List<QuestionCardResponseDTO>> getQuestionCardsWithFilterForStudent(QuestionCardFilterRequestDTO filterRequest, Long studentId);

    PaginationDTO<List<QuestionCardResponseDTO>> getQuestionCardsWithFilterForCounselor(QuestionCardFilterRequestDTO filterRequest, Long counselor);

    void takeQuestionCard(Long questionCardId, Long counselorId);

    PaginationDTO<List<QuestionCardResponseDTO>> getQuestionCardsLibraryForCounselor(QuestionCardFilterRequestDTO filterRequest, QuestionType questionType);

    QuestionCardResponseDTO getOneQuestionCardsForCounselor(Long questionCardId, Long counselorId);

    QuestionCardResponseDTO getOneQuestionCardsForStudent(Long questionCardId, Long studentId);

    void sendMessage(Long sessionId, String content, Account principle);

    void readAllMessage(Long chatSessionId, Account principle, boolean forceRead);

    void closeQuestionCardForStudent(Long questionCardId, Long studentId);

    void answerQuestionCard(AnswerQuestionCardRequestDTO dto, Long counselorId, Long questionCardId);

    void editQuestionCard(AnswerQuestionCardRequestDTO dto, Long counselorId, Long questionCardId);

    PaginationDTO<List<QuestionCardResponseDTO>> getQuestionCardsforSupportStaff(QuestionCardFilterRequestDTO filterRequest);

    void reviewQuestionCard(Long questionCardId, QuestionCardStatus questionCardStatus);

    QuestionCardResponseDTO getOneQuestionCardsForReview(Long questionCardId);

    void closeQuestionCardForCounselor(Long questionCardId, Long counselorId);

    void deleteQuestionCard(Long questionCardId, Long id);

    QuestionCardResponseDTO updateQuestionCard(CreateQuestionCardRequestDTO dto, Long studentId, Long questionCardId);

    void flagQuestionCard(Long questionCardId, FlagQuestionCardRequestDTO dto);

    BanInformationResponseDTO getBanInformation(Long studentId);
}
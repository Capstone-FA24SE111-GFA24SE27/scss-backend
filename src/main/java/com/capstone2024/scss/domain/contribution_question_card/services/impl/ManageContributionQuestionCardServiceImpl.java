package com.capstone2024.scss.domain.contribution_question_card.services.impl;

import com.capstone2024.scss.application.advice.exeptions.ForbiddenException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.contribution_question_card.dto.ContributionQuestionCardResponseDTO;
import com.capstone2024.scss.domain.common.mapper.contribution_question_card.ContributionQuestionCardMapper;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributionQuestionCard;
import com.capstone2024.scss.domain.contribution_question_card.services.ManageContributionQuestionCardService;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionCard;
import com.capstone2024.scss.infrastructure.elastic_search.documents.ContributionQuestionCardDocument;
import com.capstone2024.scss.infrastructure.elastic_search.repository.ContributionQuestionCardElasticRepository;
import com.capstone2024.scss.infrastructure.repositories.contribution_question_card.ContributionQuestionCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManageContributionQuestionCardServiceImpl implements ManageContributionQuestionCardService {

    private final ContributionQuestionCardRepository contributionQuestionCardRepository;
    private final ContributionQuestionCardElasticRepository contributionQuestionCardElasticRepository;

    @Override
    public PaginationDTO<List<ContributionQuestionCardResponseDTO>> searchContributionQuestionCards(String query, ContributionQuestionCard.PublicStatus status, Long counselorId, Long categoryId, Pageable pageable) {
        Page<ContributionQuestionCard> contributionQuestionCards = contributionQuestionCardRepository.findByFiltersWithKeyword(status, counselorId, categoryId, query, pageable);

        List<ContributionQuestionCardResponseDTO> returnValues = contributionQuestionCards.getContent().stream()
                .map(ContributionQuestionCardMapper::toResponseDTO)
                .toList();

        return PaginationDTO.<List<ContributionQuestionCardResponseDTO>>builder()
                .data(returnValues)
                .totalPages(contributionQuestionCards.getTotalPages())
                .totalElements(contributionQuestionCards.getNumberOfElements())
                .build();
    }

    @Override
    public void updatePublicStatusContributionQuestionCard(Long questionCardId, ContributionQuestionCard.PublicStatus questionCardPublicStatus) {
        ContributionQuestionCard questionCard = contributionQuestionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));
        questionCard.setPublicStatus(questionCardPublicStatus);
        contributionQuestionCardRepository.save(questionCard);

        Optional<ContributionQuestionCardDocument> documentOp = contributionQuestionCardElasticRepository.findById(String.valueOf(questionCardId));
        if (documentOp.isPresent()) {
            ContributionQuestionCardDocument document = documentOp.get();
            document.setStatus(questionCardPublicStatus.name());
            contributionQuestionCardElasticRepository.save(documentOp.get());
        }
    }
}

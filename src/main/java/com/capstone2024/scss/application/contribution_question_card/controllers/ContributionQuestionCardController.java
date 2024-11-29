package com.capstone2024.scss.application.contribution_question_card.controllers;

import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.contribution_question_card.dto.ContributionQuestionCardDTO;
import com.capstone2024.scss.application.contribution_question_card.dto.ContributionQuestionCardResponseDTO;
import com.capstone2024.scss.domain.common.mapper.contribution_question_card.ContributionQuestionCardMapper;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributionQuestionCard;
import com.capstone2024.scss.domain.contribution_question_card.services.ContributionQuestionCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contribution-question-cards")
@RequiredArgsConstructor
public class ContributionQuestionCardController {

    private final ContributionQuestionCardService questionCardService;

    @PostMapping
    public ResponseEntity<Object> createQuestionCard(@RequestBody ContributionQuestionCardDTO dto) {
        var createdCard = questionCardService.createContributionQuestionCard(
                dto.getQuestion(),
                dto.getAnswer(),
                dto.getCategoryId(),
                dto.getCounselorId()
        );

        return ResponseUtil.getResponse(ContributionQuestionCardMapper.toResponseDTO(createdCard), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateQuestionCard(
            @PathVariable Long id,
            @RequestBody ContributionQuestionCardDTO dto
    ) {
        var updatedCard = questionCardService.updateContributionQuestionCard(
                id,
                dto.getQuestion(),
                dto.getAnswer(),
                dto.getCategoryId(),
                ContributionQuestionCard.Status.UNVERIFIED,
                dto.getCounselorId()
        );

        return ResponseUtil.getResponse(ContributionQuestionCardMapper.toResponseDTO(updatedCard), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteQuestionCard(@PathVariable Long id) {
        questionCardService.deleteContributionQuestionCard(id);
        return ResponseUtil.getResponse("Delete successfully", HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchQuestionCards(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long counselorId,
            @RequestParam(required = false) Long categoryId
    ) {
        var results = questionCardService.searchContributionQuestionCards(query, status, counselorId, categoryId);

        return ResponseUtil.getResponse(results, HttpStatus.OK);
    }
}

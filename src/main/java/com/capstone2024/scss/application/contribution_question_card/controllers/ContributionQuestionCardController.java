package com.capstone2024.scss.application.contribution_question_card.controllers;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.contribution_question_card.dto.ContributionQuestionCardDTO;
import com.capstone2024.scss.application.contribution_question_card.dto.ContributionQuestionCardResponseDTO;
import com.capstone2024.scss.domain.common.mapper.contribution_question_card.ContributionQuestionCardMapper;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributionQuestionCard;
import com.capstone2024.scss.domain.contribution_question_card.services.ContributionQuestionCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                dto.getCounselorId(),
                dto.getTitle()
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
                ContributionQuestionCard.PublicStatus.VISIBLE,
                dto.getCounselorId(),
                dto.getTitle()
        );

        return ResponseUtil.getResponse(ContributionQuestionCardMapper.toResponseDTO(updatedCard), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOne(
            @PathVariable Long id
    ) {
        ContributionQuestionCardResponseDTO card = questionCardService.getOne(id);
        return ResponseUtil.getResponse(card, HttpStatus.OK);
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
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "false") boolean isSuggestion,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(
                sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        var results = questionCardService.searchContributionQuestionCards(query, status, counselorId, categoryId, pageable, isSuggestion);

        return ResponseUtil.getResponse(results, HttpStatus.OK);
    }
}

package com.capstone2024.scss.application.contribution_question_card.controllers;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.q_and_a.dto.QuestionCardFilterRequestDTO;
import com.capstone2024.scss.application.q_and_a.dto.QuestionCardResponseDTO;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributionQuestionCard;
import com.capstone2024.scss.domain.contribution_question_card.services.ManageContributionQuestionCardService;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionCard;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionCardStatus;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionType;
import com.capstone2024.scss.domain.q_and_a.service.ManageQuestionCardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manage/contribution-question-cards")
@Tag(name = "Manage Contribution Question Cards", description = "API endpoints for managing question cards")
@RequiredArgsConstructor
@Slf4j
public class ManageContributionQuestionCardController {

    private final ManageContributionQuestionCardService manageQuestionCardService;

    @GetMapping("/filter")
    public ResponseEntity<Object> searchQuestionCards(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false) ContributionQuestionCard.PublicStatus status,
            @RequestParam(required = false) Long counselorId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(
                sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        var results = manageQuestionCardService.searchContributionQuestionCards(query, status, counselorId, categoryId, pageable);

        return ResponseUtil.getResponse(results, HttpStatus.OK);
    }

    @PutMapping("/public-status/{questionCardId}/{questionCardPublicStatus}")
    public ResponseEntity<Object> acceptQC(
            @PathVariable Long questionCardId,
            @PathVariable ContributionQuestionCard.PublicStatus questionCardPublicStatus
    ) {
        manageQuestionCardService.updatePublicStatusContributionQuestionCard(questionCardId, questionCardPublicStatus);
        return ResponseUtil.getResponse("Question card's public status is update successfully" ,HttpStatus.OK);
    }
}

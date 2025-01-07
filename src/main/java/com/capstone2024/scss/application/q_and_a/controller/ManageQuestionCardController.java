package com.capstone2024.scss.application.q_and_a.controller;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.q_and_a.dto.QuestionCardFilterRequestDTO;
import com.capstone2024.scss.application.q_and_a.dto.QuestionCardResponseDTO;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionCard;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionCardStatus;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionType;
import com.capstone2024.scss.domain.q_and_a.service.ManageQuestionCardService;
import com.capstone2024.scss.domain.q_and_a.service.QuestionCardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manage/question-cards")
@Tag(name = "Manage Question Cards", description = "API endpoints for managing question cards")
@RequiredArgsConstructor
@Slf4j
public class ManageQuestionCardController {

    private final ManageQuestionCardService manageQuestionCardService;

    @GetMapping("/filter")
    public ResponseEntity<Object> getPublicQuestionCardsWithFilter(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "type", required = false) QuestionType type,
            @RequestParam(name = "status", required = false) QuestionCardStatus status,
            @RequestParam(name = "publicStatus", required = false) QuestionCard.PublicStatus publicStatus,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {

        if (page < 1) {
            log.error("Invalid page number: {}", page);
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        QuestionCardFilterRequestDTO filterRequest = QuestionCardFilterRequestDTO.builder()
                .keyword(keyword.isEmpty() ? null : keyword.trim())
                .status(status)
                .publicStatus(publicStatus)
                .isClosed(true)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, size, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .type(type)
                .build();

        PaginationDTO<List<QuestionCardResponseDTO>> responseDTO = manageQuestionCardService.getPublicQuestionCardsForManage(filterRequest);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/public-status/{questionCardId}/{questionCardPublicStatus}")
    public ResponseEntity<Object> acceptQC(
            @PathVariable Long questionCardId,
            @PathVariable QuestionCard.PublicStatus questionCardPublicStatus
            ) {
        manageQuestionCardService.updatePublicStatusQuestionCard(questionCardId, questionCardPublicStatus);
        return ResponseUtil.getResponse("Question card's public status is update successfully" ,HttpStatus.OK);
    }
}

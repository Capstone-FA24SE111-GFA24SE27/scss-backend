package com.capstone2024.scss.application.q_and_a.controller;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.q_and_a.dto.*;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionCardStatus;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionType;
import com.capstone2024.scss.domain.q_and_a.service.QuestionCardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question-cards")
@RequiredArgsConstructor
public class QuestionCardController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionCardController.class);

    private final QuestionCardService questionCardService;

    @PostMapping
    public ResponseEntity<Object> createQuestionCard(
            @Valid @RequestBody CreateQuestionCardRequestDTO dto,
            @AuthenticationPrincipal @NotNull Account principal,
            BindingResult errors) {

        if (errors.hasErrors()) {
            logger.warn("Login request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid login request", errors, HttpStatus.BAD_REQUEST);
        }

        logger.info("Received request to create QuestionCard for Account ID: {}", principal.getId());

        QuestionCardResponseDTO createdCard = questionCardService.createQuestionCard(dto, principal.getProfile().getId());

        logger.info("Successfully created QuestionCard with ID: {}", createdCard.getId());

        return ResponseUtil.getResponse(createdCard, HttpStatus.OK);
    }

    @PostMapping("/answer/{questionCardId}")
    public ResponseEntity<Object> answerQuestionCard(
            @Valid @RequestBody AnswerQuestionCardRequestDTO dto,
            @AuthenticationPrincipal @NotNull Account principal,
            @PathVariable Long questionCardId,
            BindingResult errors) {

        if (errors.hasErrors()) {
            logger.warn("answer QC request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid Param", errors, HttpStatus.BAD_REQUEST);
        }

        logger.info("Received request to answer QuestionCard for Account ID: {}", principal.getId());

        questionCardService.answerQuestionCard(dto, principal.getProfile().getId(), questionCardId);

        logger.info("Successfully answer QuestionCard");

        return ResponseUtil.getResponse("Successfully answer QuestionCard", HttpStatus.OK);
    }

    @PutMapping("/answer/edit/{questionCardId}")
    public ResponseEntity<Object> editQuestionCard(
            @Valid @RequestBody AnswerQuestionCardRequestDTO dto,
            @AuthenticationPrincipal @NotNull Account principal,
            @PathVariable Long questionCardId,
            BindingResult errors) {

        if (errors.hasErrors()) {
            logger.warn("answer QC request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid Param", errors, HttpStatus.BAD_REQUEST);
        }

        logger.info("Received request to answer QuestionCard for Account ID: {}", principal.getId());

        questionCardService.editQuestionCard(dto, principal.getProfile().getId(), questionCardId);

        logger.info("Successfully answer QuestionCard");

        return ResponseUtil.getResponse("Successfully answer QuestionCard", HttpStatus.OK);
    }

    @GetMapping("/student/filter")
    public ResponseEntity<Object> getQuestionCardsWithFilter(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "status", required = false) QuestionCardStatus status,
            @RequestParam(name = "isTaken", required = false) Boolean isTaken,
            @RequestParam(name = "isClosed", required = false) Boolean isClosed,
            @RequestParam(name = "isChatSessionClosed", required = false) Boolean isChatSessionClosed,
            @RequestParam(name = "type", required = false) QuestionType type,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @NotNull @AuthenticationPrincipal Account principle) {

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        QuestionCardFilterRequestDTO filterRequest = QuestionCardFilterRequestDTO.builder()
                .keyword(keyword.isEmpty() ? null : keyword.trim())
                .status(status)
                .isTaken(isTaken)
                .isClosed(isClosed)
                .isChatSessionClosed(isChatSessionClosed)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, 10, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .type(type)
                .build();

        PaginationDTO<List<QuestionCardResponseDTO>> responseDTO = questionCardService.getQuestionCardsWithFilterForStudent(filterRequest, principle.getProfile().getId());
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/counselor/filter")
    public ResponseEntity<Object> getQuestionCardsForCounselor(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "isClosed", required = false) Boolean isClosed,
            @RequestParam(name = "isChatSessionClosed", required = false) Boolean isChatSessionClosed,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "studentCode", required = false) String studentCode,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @NotNull @AuthenticationPrincipal Account principle) {

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        QuestionCardFilterRequestDTO filterRequest = QuestionCardFilterRequestDTO.builder()
                .keyword(keyword.isEmpty() ? null : keyword.trim())
                .isClosed(isClosed)
                .isChatSessionClosed(isChatSessionClosed)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, 10, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .studentCode(studentCode)
                .build();

        PaginationDTO<List<QuestionCardResponseDTO>> responseDTO = questionCardService.getQuestionCardsWithFilterForCounselor(filterRequest, principle.getProfile().getId());
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/counselor/take/{questionCardId}")
    public ResponseEntity<Object> takeQuestionCard(
            @PathVariable Long questionCardId,
            @NotNull @AuthenticationPrincipal Account principle) {
        questionCardService.takeQuestionCard(questionCardId, principle.getProfile().getId());
        return ResponseUtil.getResponse("Question card taken successfully and chat session created.", HttpStatus.OK);
    }

    @GetMapping("/library/non-academic-counselor/filter")
    public ResponseEntity<Object> getQuestionCardsLibraryForNonACounselor(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "isClosed", required = false) Boolean isClosed,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "studentCode", required = false) String studentCode,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        QuestionCardFilterRequestDTO filterRequest = QuestionCardFilterRequestDTO.builder()
                .keyword(keyword.isEmpty() ? null : keyword.trim())
                .isClosed(isClosed)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, 10, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .studentCode(studentCode)
                .build();

        PaginationDTO<List<QuestionCardResponseDTO>> responseDTO = questionCardService.getQuestionCardsLibraryForCounselor(filterRequest, QuestionType.NON_ACADEMIC);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/counselor/{questionCardId}")
    public ResponseEntity<Object> getOneCounselor(
            @PathVariable Long questionCardId,
            @NotNull @AuthenticationPrincipal Account principle) {

        QuestionCardResponseDTO responseDTO = questionCardService.getOneQuestionCardsForCounselor(questionCardId, principle.getProfile().getId());
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/student/{questionCardId}")
    public ResponseEntity<Object> getOneStudent(
            @PathVariable Long questionCardId,
            @NotNull @AuthenticationPrincipal Account principle) {

        QuestionCardResponseDTO responseDTO = questionCardService.getOneQuestionCardsForStudent(questionCardId, principle.getProfile().getId());
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }



    @GetMapping("/library/academic-counselor/filter")
    public ResponseEntity<Object> getQuestionCardsLibraryForACounselor(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "isClosed", required = false) Boolean isClosed,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "studentCode", required = false) String studentCode,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        QuestionCardFilterRequestDTO filterRequest = QuestionCardFilterRequestDTO.builder()
                .keyword(keyword.isEmpty() ? null : keyword.trim())
                .isClosed(isClosed)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, 10, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .studentCode(studentCode)
                .build();

        PaginationDTO<List<QuestionCardResponseDTO>> responseDTO = questionCardService.getQuestionCardsLibraryForCounselor(filterRequest, QuestionType.ACADEMIC);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/send/{sessionId}/messages")
    public ResponseEntity<String> sendMessage(
            @PathVariable Long sessionId,
            @RequestBody CreateMessageDTO createMessageDTO,
//            @RequestParam(name = "forceRead", required = false, defaultValue = "false") boolean forceRead,
            @NotNull @AuthenticationPrincipal Account principle
    ) {
        questionCardService.sendMessage(sessionId, createMessageDTO.getContent(), principle);
        return ResponseEntity.ok("Message sent successfully");
    }

    @PutMapping("/read/{chatSessionId}/messages")
    public ResponseEntity<Object> readAllMessages(
            @PathVariable Long chatSessionId,
            @NotNull @AuthenticationPrincipal Account principle) {

        questionCardService.readAllMessage(chatSessionId, principle, true);
        return ResponseUtil.getResponse("Read all message successfully" ,HttpStatus.OK);
    }

    @PostMapping("/student/close/{questionCardId}")
    public ResponseEntity<Object> closeQC(
            @PathVariable Long questionCardId,
            @NotNull @AuthenticationPrincipal Account principle) {

        questionCardService.closeQuestionCardForStudent(questionCardId, principle.getProfile().getId());
        return ResponseUtil.getResponse("Question card is closed successfully" ,HttpStatus.OK);
    }

    @PostMapping("/counselor/close/{questionCardId}")
    public ResponseEntity<Object> closeQCForCounselor(
            @PathVariable Long questionCardId,
            @NotNull @AuthenticationPrincipal Account principle) {

        questionCardService.closeQuestionCardForCounselor(questionCardId, principle.getProfile().getId());
        return ResponseUtil.getResponse("Question card is closed successfully" ,HttpStatus.OK);
    }

    @GetMapping("/review/filter")
    public ResponseEntity<Object> getQuestionCardsforSupportStaff(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "type", required = false) QuestionType type,
            @RequestParam(name = "studentCode", required = false) String studentCode,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        QuestionCardFilterRequestDTO filterRequest = QuestionCardFilterRequestDTO.builder()
                .keyword(keyword.isEmpty() ? null : keyword.trim())
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, 10, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .studentCode(studentCode)
                .type(type)
                .build();

        PaginationDTO<List<QuestionCardResponseDTO>> responseDTO = questionCardService.getQuestionCardsforSupportStaff(filterRequest);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/review/{questionCardId}/{questionCardStatus}")
    public ResponseEntity<Object> reviewQC(
            @PathVariable Long questionCardId,
            @PathVariable QuestionCardStatus questionCardStatus) {

        questionCardService.reviewQuestionCard(questionCardId, questionCardStatus);
        return ResponseUtil.getResponse("Question card is closed successfully" ,HttpStatus.OK);
    }

    @GetMapping("/review/{questionCardId}")
    public ResponseEntity<Object> getOneReviewQC(
            @PathVariable Long questionCardId) {

        QuestionCardResponseDTO responseDTO = questionCardService.getOneQuestionCardsForReview(questionCardId);
        return ResponseUtil.getResponse(responseDTO ,HttpStatus.OK);
    }
}

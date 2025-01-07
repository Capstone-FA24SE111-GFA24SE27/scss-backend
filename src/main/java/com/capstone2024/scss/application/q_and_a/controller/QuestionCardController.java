package com.capstone2024.scss.application.q_and_a.controller;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.booking_counseling.dto.request.counceling_appointment.AppointmentFeedbackDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.q_and_a.dto.*;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionCardStatus;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionType;
import com.capstone2024.scss.domain.q_and_a.service.QuestionCardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/question-cards")
@Tag(name = "Question Cards", description = "API endpoints for managing question cards")
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

    @PostMapping("/student/chat-session/create/{questionCardId}")
    public ResponseEntity<Object> createChatSessionForQuestionCard(
            @AuthenticationPrincipal @NotNull Account principal,
            @PathVariable Long questionCardId) {

        logger.info("Received request to create chat session QuestionCard for Account ID: {}", principal.getId());

        Long studentId = principal.getProfile().getId();

        questionCardService.createChatSessionForQuestionCard(studentId, questionCardId);

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

    @GetMapping("/filter")
    public ResponseEntity<Object> getPublicQuestionCardsWithFilter(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "type", required = false) QuestionType type,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(required = false, defaultValue = "false") boolean isSuggestion,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        QuestionCardFilterRequestDTO filterRequest = QuestionCardFilterRequestDTO.builder()
                .keyword(keyword.isEmpty() ? null : keyword.trim())
                .status(QuestionCardStatus.VERIFIED)
                .isClosed(true)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, size, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .type(type)
                .build();

        PaginationDTO<List<QuestionCardResponseDTO>> responseDTO = questionCardService.getPublicQuestionCardsWithFilterForStudent(filterRequest, isSuggestion);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/student/filter")
    public ResponseEntity<Object> getQuestionCardsWithFilter(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "status", required = false) QuestionCardStatus status,
            @RequestParam(name = "isClosed", required = false) Boolean isClosed,
            @RequestParam(name = "type", required = false) QuestionType type,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "topicId", required = false) Long topicId,
            @NotNull @AuthenticationPrincipal Account principle) {

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        QuestionCardFilterRequestDTO filterRequest = QuestionCardFilterRequestDTO.builder()
                .keyword(keyword.isEmpty() ? null : keyword.trim())
                .status(status)
                .isClosed(isClosed)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, size, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .type(type)
                .topicId(topicId)
                .build();

        PaginationDTO<List<QuestionCardResponseDTO>> responseDTO = questionCardService.getQuestionCardsWithFilterForStudent(filterRequest, principle.getProfile().getId());
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/manage/student/filter/{studentId}")
    public ResponseEntity<Object> getQuestionCardsWithFilter(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "status", required = false) QuestionCardStatus status,
            @RequestParam(name = "isClosed", required = false) Boolean isClosed,
            @RequestParam(name = "type", required = false) QuestionType type,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @PathVariable Long studentId) {

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        QuestionCardFilterRequestDTO filterRequest = QuestionCardFilterRequestDTO.builder()
                .keyword(keyword.isEmpty() ? null : keyword.trim())
                .status(status)
                .isClosed(isClosed)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, 10, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .type(type)
                .build();

        PaginationDTO<List<QuestionCardResponseDTO>> responseDTO = questionCardService.getQuestionCardsWithFilterForStudent(filterRequest, studentId);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/counselor/filter")
    public ResponseEntity<Object> getQuestionCardsForCounselor(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "isClosed", required = false) Boolean isClosed,
            @RequestParam(name = "status", required = false) QuestionCardStatus status,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "studentCode", required = false) String studentCode,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @NotNull @AuthenticationPrincipal Account principle) {

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        QuestionCardFilterRequestDTO filterRequest = QuestionCardFilterRequestDTO.builder()
                .keyword(keyword.isEmpty() ? null : keyword.trim())
                .isClosed(isClosed)
                .status(status)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, size, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .studentCode(studentCode)
                .from(from)
                .to(to)
                .build();

        PaginationDTO<List<QuestionCardResponseDTO>> responseDTO = questionCardService.getQuestionCardsWithFilterForCounselor(filterRequest, principle.getProfile().getId());
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/manage/counselor/filter/{counselorId}")
    public ResponseEntity<Object> getQuestionCardsForCounselorForManage(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "isClosed", required = false) Boolean isClosed,
            @RequestParam(name = "status", required = false) QuestionCardStatus status,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "studentCode", required = false) String studentCode,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "topicId", required = false) Long topicId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PathVariable long counselorId) {

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        QuestionCardFilterRequestDTO filterRequest = QuestionCardFilterRequestDTO.builder()
                .keyword(keyword.isEmpty() ? null : keyword.trim())
                .isClosed(isClosed)
                .status(status)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, size, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .studentCode(studentCode)
                .topicId(topicId)
                .from(from)
                .to(to)
                .build();

        PaginationDTO<List<QuestionCardResponseDTO>> responseDTO = questionCardService.getQuestionCardsWithFilterForCounselorForManage(filterRequest, counselorId);
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
            @RequestParam(name = "topicId", required = false) Long topicId,
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
                .topicId(topicId)
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

    @GetMapping("/manage/find-all")
    public ResponseEntity<Object> getAllQuestionCard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<QuestionCardResponseDTO> responseDTO = questionCardService.getAll(from, to);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/student/{questionCardId}")
    public ResponseEntity<Object> getOneStudent(
            @PathVariable Long questionCardId,
            @NotNull @AuthenticationPrincipal Account principle) {

        QuestionCardResponseDTO responseDTO = questionCardService.getOneQuestionCardsForStudent(questionCardId, principle.getProfile().getId());
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/student/message/{questionCardId}")
    public ResponseEntity<Object> getOneChatSessionForStudent(
            @PathVariable Long questionCardId,
            @NotNull @AuthenticationPrincipal Account principle) {

        Long studentId = principle.getProfile().getId();

        ChatSessionDTO responseDTO = questionCardService.getMessageByChatSessionForStudent(questionCardId, studentId);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/counselor/message/{questionCardId}")
    public ResponseEntity<Object> getOneChatSessionForCounselor(
            @PathVariable Long questionCardId,
            @NotNull @AuthenticationPrincipal Account principle) {

        Long counselorId = principle.getProfile().getId();

        ChatSessionDTO responseDTO = questionCardService.getMessageByChatSessionForcounselor(questionCardId, counselorId);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/message/{questionCardId}")
    public ResponseEntity<Object> getOneChatSession(
            @PathVariable Long questionCardId,
            @NotNull @AuthenticationPrincipal Account principle) {

        Long id = principle.getProfile().getId();
        Role role = principle.getRole();

        ChatSessionDTO responseDTO = questionCardService.getMessageByChatSession(questionCardId, id, role);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{questionCardId}")
    public ResponseEntity<Object> deleteQCard(
            @PathVariable Long questionCardId,
            @NotNull @AuthenticationPrincipal Account principle) {

        questionCardService.deleteQuestionCard(questionCardId, principle.getProfile().getId());
        return ResponseUtil.getResponse("Delete successfully", HttpStatus.OK);
    }

    @PutMapping("/edit/{questionCardId}")
    public ResponseEntity<Object> updateQuestionCard(
            @Valid @RequestBody CreateQuestionCardRequestDTO dto,
            @AuthenticationPrincipal @NotNull Account principal,
            @PathVariable Long questionCardId,
            BindingResult errors) {

        if (errors.hasErrors()) {
            logger.warn("validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid login request", errors, HttpStatus.BAD_REQUEST);
        }

        logger.info("Received request to update QuestionCard for Account ID: {}", principal.getId());

        QuestionCardResponseDTO updatedCard = questionCardService.updateQuestionCard(dto, principal.getProfile().getId(), questionCardId);

        logger.info("Successfully updated QuestionCard with ID: {}", updatedCard.getId());

        return ResponseUtil.getResponse(updatedCard, HttpStatus.OK);
    }

    @GetMapping("/library/academic-counselor/filter")
    public ResponseEntity<Object> getQuestionCardsLibraryForACounselor(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "isClosed", required = false) Boolean isClosed,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "studentCode", required = false) String studentCode,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "topicId", required = false) Long topicId,
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
                .topicId(topicId)
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
        System.out.println("hello");
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
            @PathVariable QuestionCardStatus questionCardStatus,
            @RequestParam(name = "reviewReason", defaultValue = "") String reviewReason) {

        questionCardService.reviewQuestionCard(questionCardId, questionCardStatus, reviewReason);
        return ResponseUtil.getResponse("Question card is review successfully" ,HttpStatus.OK);
    }

    @PostMapping("/review/flag/{questionCardId}")
    public ResponseEntity<Object> flagQC(
            @PathVariable Long questionCardId,
            @Valid @RequestBody FlagQuestionCardRequestDTO dto,
            BindingResult errors) {

        if (errors.hasErrors()) {
            logger.warn("validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid", errors, HttpStatus.BAD_REQUEST);
        }

        questionCardService.flagQuestionCard(questionCardId, dto);
        return ResponseUtil.getResponse("Question card is flag successfully" ,HttpStatus.OK);
    }

    @GetMapping("/review/{questionCardId}")
    public ResponseEntity<Object> getOneReviewQC(
            @PathVariable Long questionCardId) {

        QuestionCardResponseDTO responseDTO = questionCardService.getOneQuestionCardsForReview(questionCardId);
        return ResponseUtil.getResponse(responseDTO ,HttpStatus.OK);
    }

    @GetMapping("/student/ban-info")
    public ResponseEntity<BanInformationResponseDTO> getBanInformation(@AuthenticationPrincipal @NotNull Account principal) {
        BanInformationResponseDTO response = questionCardService.getBanInformation(principal.getProfile().getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/feedback/{questionCardId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Submit feedback for an appointment",
            description = "Submit feedback for an appointment"
    )
    public ResponseEntity<Object> submitFeedback(@PathVariable Long questionCardId,
                                                 @Valid @RequestBody AppointmentFeedbackDTO feedbackDTO,
                                                 BindingResult bindingResult,
                                                 @AuthenticationPrincipal @NotNull Account principal) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult, HttpStatus.BAD_REQUEST);
        }

        questionCardService.submitFeedback(questionCardId, feedbackDTO, principal.getProfile().getId());

        return ResponseUtil.getResponse("Feedback submitted successfully", HttpStatus.OK);
    }

    @PostMapping("/accept/{questionCardId}")
    public ResponseEntity<Object> acceptQC(
            @PathVariable Long questionCardId) {
        questionCardService.acceptQuestionCard(questionCardId);
        return ResponseUtil.getResponse("Question card is review successfully" ,HttpStatus.OK);
    }

    @GetMapping("/count-open/{studentId}")
    public ResponseEntity<Object> countOpenQuestionCards(@PathVariable Long studentId) {
        long count = questionCardService.countOpenQuestionCardsByStudent(studentId);
        return ResponseUtil.getResponse(count, HttpStatus.OK);
    }
}

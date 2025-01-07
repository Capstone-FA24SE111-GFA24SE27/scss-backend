package com.capstone2024.scss.domain.q_and_a.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.advice.exeptions.ForbiddenException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.booking_counseling.dto.request.counceling_appointment.AppointmentFeedbackDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.notification.dtos.NotificationDTO;
import com.capstone2024.scss.application.q_and_a.dto.*;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.common.helpers.NotificationHelper;
import com.capstone2024.scss.domain.common.mapper.q_and_a.ChatSessionMapper;
import com.capstone2024.scss.domain.common.mapper.q_and_a.QuestionCardMapper;
import com.capstone2024.scss.domain.counselor.entities.AcademicCounselor;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.counselor.entities.Expertise;
import com.capstone2024.scss.domain.counselor.entities.NonAcademicCounselor;
import com.capstone2024.scss.domain.notification.services.NotificationService;
import com.capstone2024.scss.domain.q_and_a.entities.*;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionCardStatus;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionType;
import com.capstone2024.scss.domain.q_and_a.service.QuestionCardService;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.infrastructure.configuration.openai.OpenAIService;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.RabbitMQConfig;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto.RealTimeAppointmentDTO;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto.RealTimeQuestionDTO;
import com.capstone2024.scss.infrastructure.elastic_search.documents.QuestionCardDocument;
import com.capstone2024.scss.infrastructure.elastic_search.repository.QuestionCardDocumentRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.AcademicCounselorRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.ExpertiseRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.NonAcademicCounselorRepository;
import com.capstone2024.scss.infrastructure.repositories.student.StudentRepository;
import com.capstone2024.scss.infrastructure.repositories._and_a.*;
import com.capstone2024.scss.infrastructure.repositories.counselor.CounselorRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionCardServiceImpl implements QuestionCardService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionCardServiceImpl.class);

    private final QuestionCardRepository questionCardRepository;
    private final StudentRepository studentRepository;
    private final CounselorRepository counselorRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final MessageRepository messageRepository;
    private final RabbitTemplate rabbitTemplate;
    private final NotificationService notificationService;
    private final QuestionFlagRepository questionFlagRepository;
    private final QuestionBanRepository questionBanRepository;
    private final TopicRepository topicRepository;
    private final AcademicCounselorRepository academicCounselorRepository;
    private final NonAcademicCounselorRepository nonAcademicCounselorRepository;
    private final OpenAIService openAIService;
    private final ExpertiseRepository expertiseRepository;
    private final QuestionCardDocumentRepository questionCardDocumentRepository;
    private final ElasticsearchClient elasticsearchClient;
    private final QuestionCardFeedbackRepository questionCardFeedbackRepository;

    @Override
    @Transactional
    public QuestionCardResponseDTO createQuestionCard(CreateQuestionCardRequestDTO dto, Long studentId) {
        logger.info("Starting creation of QuestionCard for Account ID: {}", studentId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student not found for Account ID: {}", studentId);
                    return new NotFoundException("Student not found for the account");
                });

        long count = questionCardRepository.countNotClosedQuestionCardsByStudent(student);

        if(count > 3) {
            throw new ForbiddenException("Can not create question because there are higher than 3 opened question");
        }

        QuestionCard questionCard = null;

        if(dto.getQuestionType().equals(QuestionType.ACADEMIC)) {

            List<AcademicCounselor> counselors = academicCounselorRepository.findAcademicCounselorWithLeastQuestions(
                    dto.getDepartmentId(),
                    dto.getMajorId()
//                    student.getDepartment().getId(),
//                    student.getMajor().getId()
//                    null
            );

            AcademicCounselor counselor = null;

            if(!counselors.isEmpty()) {
                counselor = counselors.getFirst();
            } else {
                throw new NotFoundException("There is no counselor match");
            }

            // Tạo thẻ câu hỏi mới
            questionCard = QuestionCard.builder()
                    .content(dto.getContent())
                    .questionType(dto.getQuestionType())
                    .counselor(counselor)
                    .student(student)
                    .status(QuestionCardStatus.PENDING) // Trạng thái mặc định
                    .publicStatus(QuestionCard.PublicStatus.PENDING)
//                    .isTaken(false)
                    .isClosed(false)
                    .isAccepted(false)
                    .title(dto.getTitle())
//                    .topic(topic)
                    .build();
        } else if(dto.getQuestionType().equals(QuestionType.NON_ACADEMIC)) {
//            String prompt = openAIService.generatePromptToOpenAIForBestExpertiseMatching(dto.getContent());
//            String expertiseName = openAIService.callOpenAPIForBestExpertiseMatching(prompt);
//
//            Expertise expertise = expertiseRepository.findByName(expertiseName)
//                    .orElseThrow(() -> new NotFoundException("Expertise not found with name: " + expertiseName));
            Expertise expertise = expertiseRepository.findById(dto.getExpertiseId())
                    .orElseThrow(() -> new NotFoundException("Expertise not found with id: " + dto.getExpertiseId()));

            List<NonAcademicCounselor> counselors = nonAcademicCounselorRepository.findNonAcademicCounselorWithLeastQuestions(
                    expertise.getId()
            );

            NonAcademicCounselor counselor = null;

            if(counselors.size() > 0) {
                counselor = counselors.getFirst();
            } else {
                throw new NotFoundException("There is no counselor match");
            }

            // Tạo thẻ câu hỏi mới
            questionCard = QuestionCard.builder()
                    .content(dto.getContent())
                    .questionType(dto.getQuestionType())
                    .counselor(counselor)
                    .student(student)
                    .status(QuestionCardStatus.PENDING) // Trạng thái mặc định
                    .publicStatus(QuestionCard.PublicStatus.PENDING)
//                    .isTaken(false)
                    .isClosed(false)
                    .isAccepted(false)
                    .build();
        } else {
            throw new BadRequestException("Invalid question type");
        }

        if(questionCard != null) {

            String prompt = openAIService.generatePromptToOpenAIForAdjustDifficultyLevel(dto.getContent());
            String difficultyLevel = openAIService.callOpenAPIForAdjustDifficultyLevel(prompt);

            QuestionCard.QuestionCarDifficultyLevel difficultyLevelEnum = QuestionCard.QuestionCarDifficultyLevel.valueOf(difficultyLevel);
            questionCard.setDifficultyLevel(difficultyLevelEnum);
            questionCard.setTitle(dto.getTitle());

            // Lưu thẻ câu hỏi vào cơ sở dữ liệu
            QuestionCard savedCard = questionCardRepository.save(questionCard);
            logger.info("QuestionCard created with ID: {} for Student ID: {}", savedCard.getId(), student.getId());

            rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_Q_A, RealTimeQuestionDTO.builder()
                    .studentId(questionCard.getStudent().getId())
                    .counselorId(questionCard.getCounselor() != null ? questionCard.getCounselor().getId() : null)
                    .type(RealTimeQuestionDTO.Type.STUDENT_CREATE_NEW)
                    .build());

            notificationService.sendNotification(NotificationDTO.builder()
                    .receiverId(questionCard.getCounselor().getId())
                    .message("Student named -" + student.getFullName() + "-" + student.getStudentCode() + "- has sent you a question")
                    .title("New question card from student")
                    .sender("Student: " + student.getFullName() + "-" + student.getStudentCode())
                    .readStatus(false)
                    .build());

            // Chuyển đổi entity sang DTO
            return QuestionCardMapper.toQuestionCardResponseDto(savedCard);
        } else {
            throw new RuntimeException("System Error");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationDTO<List<QuestionCardResponseDTO>> getQuestionCardsWithFilterForStudent(QuestionCardFilterRequestDTO filterRequest, Long studentId) {
        logger.info("Fetching Question Cards with filters: {}", filterRequest);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentId));

        Page<QuestionCard> questionCardsPage = new PageImpl<>(Collections.emptyList(), filterRequest.getPagination(), 0);

        questionCardsPage = questionCardRepository.findQuestionCardsWithFilterForStudent(
                studentId,
                filterRequest.getKeyword(),
                filterRequest.getStatus(),
//                filterRequest.getIsTaken(),
                filterRequest.getIsClosed(),
                filterRequest.getType(),
//                filterRequest.getTopicId(),
                filterRequest.getPagination()
        );

        List<QuestionCardResponseDTO> questionCardDTOs = questionCardsPage.getContent().stream()
                .map(QuestionCardMapper::toQuestionCardResponseDto)
                .collect(Collectors.toList());

        return PaginationDTO.<List<QuestionCardResponseDTO>>builder()
                .data(questionCardDTOs)
                .totalPages(questionCardsPage.getTotalPages())
                .totalElements((int) questionCardsPage.getTotalElements())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationDTO<List<QuestionCardResponseDTO>> getQuestionCardsWithFilterForCounselor(QuestionCardFilterRequestDTO filterRequest, Long counselorId) {
        logger.info("Fetching Question Cards with filters: {}", filterRequest);

        LocalDateTime fromDateTime = (filterRequest.getFrom() != null) ? filterRequest.getFrom().atStartOfDay() : null;
        LocalDateTime toDateTime = (filterRequest.getTo() != null) ? filterRequest.getTo().atTime(LocalTime.MAX) : null;

        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found with ID: " + counselorId));

        Page<QuestionCard> questionCardsPage = new PageImpl<>(Collections.emptyList(), filterRequest.getPagination(), 0);

        if(counselor instanceof NonAcademicCounselor) {
            questionCardsPage = questionCardRepository.findQuestionCardsWithFilterForCounselor(
                    counselorId,
                    filterRequest.getKeyword(),
                    QuestionType.NON_ACADEMIC,
                    filterRequest.getIsClosed(),
                    filterRequest.getStatus(),
                    fromDateTime,
                    toDateTime,
                    filterRequest.getPagination()
            );
        } else  {
            questionCardsPage = questionCardRepository.findQuestionCardsWithFilterForCounselor(
                    counselorId,
                    filterRequest.getKeyword(),
                    QuestionType.ACADEMIC,
                    filterRequest.getIsClosed(),
//                    filterRequest.getTopicId(),
                    filterRequest.getStatus(),
                    fromDateTime,
                    toDateTime,
                    filterRequest.getPagination()
            );
        }

        List<QuestionCardResponseDTO> questionCardDTOs = questionCardsPage.getContent().stream()
                .map(QuestionCardMapper::toQuestionCardResponseDto)
                .collect(Collectors.toList());

        return PaginationDTO.<List<QuestionCardResponseDTO>>builder()
                .data(questionCardDTOs)
                .totalPages(questionCardsPage.getTotalPages())
                .totalElements((int) questionCardsPage.getTotalElements())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationDTO<List<QuestionCardResponseDTO>> getQuestionCardsWithFilterForCounselorForManage(QuestionCardFilterRequestDTO filterRequest, Long counselorId) {
        logger.info("Fetching Question Cards with filters: {}", filterRequest);
        LocalDateTime fromDateTime = (filterRequest.getFrom() != null) ? filterRequest.getFrom().atStartOfDay() : null;
        LocalDateTime toDateTime = (filterRequest.getTo() != null) ? filterRequest.getTo().atTime(LocalTime.MAX) : null;

        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found with ID: " + counselorId));

        Page<QuestionCard> questionCardsPage = new PageImpl<>(Collections.emptyList(), filterRequest.getPagination(), 0);

        if(counselor instanceof NonAcademicCounselor) {
            questionCardsPage = questionCardRepository.findQuestionCardsWithFilterForCounselorForManage(
                    counselorId,
                    filterRequest.getKeyword(),
                    QuestionType.NON_ACADEMIC,
                    filterRequest.getIsClosed(),
                    filterRequest.getStatus(),
                    fromDateTime,
                    toDateTime,
                    filterRequest.getPagination()
            );
        } else  {
            questionCardsPage = questionCardRepository.findQuestionCardsWithFilterForCounselorForManage(
                    counselorId,
                    filterRequest.getKeyword(),
                    QuestionType.ACADEMIC,
                    filterRequest.getIsClosed(),
                    filterRequest.getStatus(),
                    fromDateTime,
                    toDateTime,
                    filterRequest.getPagination()
            );
        }

        List<QuestionCardResponseDTO> questionCardDTOs = questionCardsPage.getContent().stream()
                .map(QuestionCardMapper::toQuestionCardResponseDto)
                .collect(Collectors.toList());

        return PaginationDTO.<List<QuestionCardResponseDTO>>builder()
                .data(questionCardDTOs)
                .totalPages(questionCardsPage.getTotalPages())
                .totalElements((int) questionCardsPage.getTotalElements())
                .build();
    }

    @Override
    public void takeQuestionCard(Long questionCardId, Long counselorId) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found with ID: " + counselorId));

//        if (questionCard.isTaken()) {
//            throw new BadRequestException("Question card has already been taken.");
//        }

        if (questionCard.isClosed()) {
            throw new BadRequestException("Question card is closed and cannot be taken.");
        }

//        questionCard.setTaken(true);
        questionCard.setCounselor(counselor);
        questionCardRepository.save(questionCard);

        ChatSession chatSession = new ChatSession();
        chatSession.setQuestionCard(questionCard);
        chatSession.setCounselor(counselor);

        Student student = questionCard.getStudent();

        chatSession.setStudent(student);

        chatSessionRepository.save(chatSession);

        notificationService.sendNotification(NotificationDTO.builder()
                .receiverId(questionCard.getStudent().getId())
                .message("Counselor named -" + counselor.getFullName() + "-" + "- has taken your question card")
                .title("Your question card has been taken")
                .sender("Counselor: " + counselor.getFullName())
                .readStatus(false)
                .build());
    }

    @Override
    public PaginationDTO<List<QuestionCardResponseDTO>> getQuestionCardsLibraryForCounselor(QuestionCardFilterRequestDTO filterRequest, QuestionType questionType) {
        logger.info("Fetching Question Cards with filters: {}", filterRequest);

        Page<QuestionCard> questionCardsPage = questionCardRepository.findQuestionCardsLibraryForCounselor(
                    filterRequest.getStudentCode(),
                    filterRequest.getKeyword(),
                    questionType,
                    false,
//                    false,
//                    filterRequest.getTopicId(),
                    filterRequest.getPagination());

        List<QuestionCardResponseDTO> questionCardDTOs = questionCardsPage.getContent().stream()
                .map(QuestionCardMapper::toQuestionCardResponseDto)
                .collect(Collectors.toList());

        return PaginationDTO.<List<QuestionCardResponseDTO>>builder()
                .data(questionCardDTOs)
                .totalPages(questionCardsPage.getTotalPages())
                .totalElements((int) questionCardsPage.getTotalElements())
                .build();
    }

    @Override
    public QuestionCardResponseDTO getOneQuestionCardsForCounselor(Long questionCardId, Long counselorId) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found with ID: " + counselorId));

        if (!questionCard.getCounselor().getId().equals(counselorId)) {
            throw new ForbiddenException("You do not own this card");
        }

        return QuestionCardMapper.toQuestionCardResponseDto(questionCard);
    }

    @Override
    public QuestionCardResponseDTO getOneQuestionCardsForStudent(Long questionCardId, Long studentId) {
        QuestionCard questionCard = questionCardRepository.findByIdWithCounselor(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentId));

        if (!questionCard.getStudent().getId().equals(studentId)) {
            throw new ForbiddenException("You do not own this card");
        }

        return QuestionCardMapper.toQuestionCardResponseDto(questionCard);
    }

    @Override
    public void sendMessage(Long sessionId, String content, Account principle) {
        ChatSession chatSession = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Chat session not found"));

        Message message = new Message();
        message.setChatSession(chatSession);
        message.setContent(content);

        ZoneId vietnam = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nowVietNam = now.withZoneSameInstant(vietnam);
        LocalDateTime vietNamDateTime = nowVietNam.toLocalDateTime();

//        LocalDateTime now = LocalDateTime.now();
        message.setSentAt(vietNamDateTime);
        message.setRead(false);

        if (!chatSession.isClosed()) {
            if (principle.getRole().equals(Role.STUDENT)) {
                if(chatSession.getStudent().getId().equals(principle.getProfile().getId())) {
                    message.setSender(principle);
                    chatSession.setLastInteractionDate(LocalDateTime.now());
                } else {
                    throw new ForbiddenException("You are not allow to send message in this session");
                }
            } else if (principle.getProfile() instanceof Counselor) {
                if(chatSession.getCounselor().getId().equals(principle.getProfile().getId())) {
                    message.setSender(principle);
                    chatSession.setLastInteractionDate(LocalDateTime.now());
                } else {
                    throw new ForbiddenException("You are not allow to send message in this session");
                }
            }
        } else {
            throw new BadRequestException("This session is close");
        }

        message = messageRepository.save(message);
        chatSessionRepository.save(chatSession);

        rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_CHAT_SESSION, ChatSessionMapper.toMessageDTODtoWithSessionId(message, chatSession.getId()));
    }

    @Override
    public void readAllMessage(Long chatSessionId, Account principle, boolean forceRead) {
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId)
                .orElseThrow(() -> new NotFoundException("Chat session not found"));
        if(principle.getRole().equals(Role.STUDENT)) {
            Counselor counselor = chatSession.getCounselor();
            Account counselorAccount = counselor.getAccount();
            if (chatSession.getStudent().getId().equals(principle.getProfile().getId())) {
                messageRepository.readAllMessages(chatSessionId, counselorAccount, forceRead);
            } else {
                throw new ForbiddenException("You are not allow to read message in this session");
            }
        } else if (principle.getProfile() instanceof Counselor) {
            Student student = chatSession.getStudent();
            Account studentAccount = student.getAccount();
            if (chatSession.getCounselor().getId().equals(principle.getProfile().getId())) {
                messageRepository.readAllMessages(chatSessionId, studentAccount, forceRead);
            } else {
                throw new ForbiddenException("You are not allow to read message in this session");
            }
        }
    }

    @Override
    public void closeQuestionCardForStudent(Long questionCardId, Long studentId) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));
        if(questionCard.getStudent().getId().equals(studentId)) {
            questionCard.setClosed(true);
            questionCardRepository.save(questionCard);

            rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_Q_A, RealTimeQuestionDTO.builder()
                    .studentId(questionCard.getStudent().getId())
                    .counselorId(questionCard.getCounselor() != null ? questionCard.getCounselor().getId() : null)
                    .type(RealTimeQuestionDTO.Type.STUDENT_CLOSE)
                    .build());

            notificationService.sendNotification(NotificationDTO.builder()
                    .receiverId(questionCard.getCounselor().getId())
                    .message("Student has closed question")
                    .title("Student has closed question")
                    .sender("Student: " + questionCard.getStudent().getFullName())
                    .readStatus(false)
                    .build());
        } else {
            throw new ForbiddenException("You are not allowed to close this QC");
        }
    }

    @Override
    public void answerQuestionCard(AnswerQuestionCardRequestDTO dto, Long counselorId, Long questionCardId) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        if (questionCard.getCounselor().getId().equals(counselorId) && !questionCard.isClosed()) {
            questionCard.setAnswer(dto.getContent());
            questionCard.setStatus(QuestionCardStatus.VERIFIED);
            questionCardRepository.save(questionCard);

            rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_Q_A, RealTimeQuestionDTO.builder()
                    .studentId(questionCard.getStudent().getId())
                    .counselorId(questionCard.getCounselor() != null ? questionCard.getCounselor().getId() : null)
                    .type(RealTimeQuestionDTO.Type.COUNSELOR_ANSWER)
                    .build());

            Student student = questionCard.getStudent();

            notificationService.sendNotification(NotificationDTO.builder()
                    .receiverId(student.getId())
                    .message("Counselor has answered your question")
                    .title("Counselor has answered your question")
                    .sender("Counselor: " + questionCard.getCounselor().getFullName())
                    .readStatus(false)
                    .build());
        } else {
            throw new ForbiddenException("You are not allow to answer this QC");
        }
    }

    @Override
    public void editQuestionCard(AnswerQuestionCardRequestDTO dto, Long counselorId, Long questionCardId) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        if (questionCard.getCounselor().getId().equals(counselorId) && !questionCard.isClosed()) {
            questionCard.setAnswer(dto.getContent());
            questionCardRepository.save(questionCard);

            rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_Q_A, RealTimeQuestionDTO.builder()
                    .studentId(questionCard.getStudent().getId())
                    .counselorId(questionCard.getCounselor() != null ? questionCard.getCounselor().getId() : null)
                    .type(RealTimeQuestionDTO.Type.COUNSELOR_EDIT_ANSWER)
                    .build());
        } else {
            throw new ForbiddenException("You are not allow to edit this QC");
        }
    }

    @Override
    public PaginationDTO<List<QuestionCardResponseDTO>> getQuestionCardsforSupportStaff(QuestionCardFilterRequestDTO filterRequest) {
        Page<QuestionCard> questionCardsPage = questionCardRepository.findQuestionCardsforSupportStaff(
                filterRequest.getStudentCode(),
                filterRequest.getKeyword(),
                filterRequest.getType(),
                filterRequest.getPagination());

        List<QuestionCardResponseDTO> questionCardDTOs = questionCardsPage.getContent().stream()
                .map(QuestionCardMapper::toQuestionCardResponseDto)
                .collect(Collectors.toList());

        return PaginationDTO.<List<QuestionCardResponseDTO>>builder()
                .data(questionCardDTOs)
                .totalPages(questionCardsPage.getTotalPages())
                .totalElements((int) questionCardsPage.getTotalElements())
                .build();
    }

    @Override
    public void reviewQuestionCard(Long questionCardId, QuestionCardStatus questionCardStatus, String reviewReason) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        switch (questionCardStatus) {
            case VERIFIED -> {
                if(!questionCard.getStatus().equals(QuestionCardStatus.PENDING)) {
                    throw new ForbiddenException("This card is not PENDING");
                }
                questionCard.setStatus(QuestionCardStatus.VERIFIED);
                questionCardRepository.save(questionCard);
            }
            case REJECTED -> {
                if(questionCard.getStatus().equals(QuestionCardStatus.VERIFIED)) {
                    throw new ForbiddenException("This card is verified");
                }
                questionCard.setStatus(QuestionCardStatus.REJECTED);
                questionCard.setReviewReason(reviewReason);
                questionCardRepository.save(questionCard);
            }
            default -> throw new BadRequestException("Invalid status");
        }

        rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_Q_A, RealTimeQuestionDTO.builder()
                .studentId(questionCard.getStudent().getId())
                .counselorId(questionCard.getCounselor() != null ? questionCard.getCounselor().getId() : null)
                .type(RealTimeQuestionDTO.Type.REVIEW)
                .build());
    }

    @Override
    public QuestionCardResponseDTO getOneQuestionCardsForReview(Long questionCardId) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        return QuestionCardMapper.toQuestionCardResponseDto(questionCard);
    }

    @Override
    public void closeQuestionCardForCounselor(Long questionCardId, Long counselorId) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));
        if(questionCard.getCounselor().getId().equals(counselorId)) {
            questionCard.setClosed(true);
            questionCard.setPublicStatus(QuestionCard.PublicStatus.VISIBLE);
            questionCardRepository.save(questionCard);
//            ChatSession chatSession = questionCard.getChatSession();
//            chatSession.setClosed(true);
//            chatSessionRepository.save(chatSession);

            // Lấy vector embedding cho question và title từ OpenAI API
            List<Double> questionVectorDouble = openAIService.getEmbeddingFromOpenAPI(questionCard.getContent());
            List<Double> titleVectorDouble = openAIService.getEmbeddingFromOpenAPI(questionCard.getTitle());

            // Chuyển đổi Double -> Float
            List<Float> questionVector = questionVectorDouble.stream()
                    .map(Double::floatValue)
                    .toList();
            List<Float> titleVector = titleVectorDouble.stream()
                    .map(Double::floatValue)
                    .toList();

            // Tạo QuestionCardDocument cho Elasticsearch và lưu vào Elasticsearch
            QuestionCardDocument questionCardDocument = QuestionCardDocument.builder()
                    .id(String.valueOf(questionCard.getId())) // ID from the QuestionCard
                    .sortingId(questionCard.getId())
                    .title(questionCard.getTitle())
                    .content(questionCard.getContent())
                    .answer(questionCard.getAnswer()) // Answers for the document
                    .questionType(questionCard.getQuestionType().name()) // Converting enum to string
                    .difficultyLevel(questionCard.getDifficultyLevel().name()) // Converting enum to string
                    .closedDate(questionCard.getClosedDate()) // The current date for the created time
                    .counselorId(String.valueOf(questionCard.getCounselor().getId())) // Counselor ID
                    .studentId(String.valueOf(questionCard.getStudent().getId())) // Randomly select student ID
                    .contentVector(questionVector)
                    .titleVector(titleVector)
                    .build();

            questionCardDocumentRepository.save(questionCardDocument);

            rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_Q_A, RealTimeQuestionDTO.builder()
                    .studentId(questionCard.getStudent().getId())
                    .counselorId(questionCard.getCounselor().getId())
                    .type(RealTimeQuestionDTO.Type.COUNSELOR_CLOSE)
                    .build());

            notificationService.sendNotification(NotificationDTO.builder()
                    .receiverId(questionCard.getStudent().getId())
                    .message("Counselor has closed your question")
                    .title("Counselor has closed your question")
                    .sender("Counselor: " + questionCard.getCounselor().getFullName())
                    .readStatus(false)
                    .build());
        } else {
            throw new ForbiddenException("You are not allowed to close this QC");
        }
    }

    @Override
    public void deleteQuestionCard(Long questionCardId, Long id) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        if (questionCard.getStudent().getId().equals(id) && questionCard.getStatus().equals(QuestionCardStatus.PENDING)) {
            questionCardRepository.deleteById(questionCardId);

            rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_Q_A, RealTimeQuestionDTO.builder()
                    .studentId(questionCard.getStudent().getId())
                    .counselorId(null)
                    .type(RealTimeQuestionDTO.Type.STUDENT_DELETE)
                    .build());
        } else {
            throw new ForbiddenException("You are not allow to delete this card");
        }
    }

    @Override
    public QuestionCardResponseDTO updateQuestionCard(CreateQuestionCardRequestDTO dto, Long studentId, Long questionCardId) {
        logger.info("Starting updating of QuestionCard for Account ID: {}", studentId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student not found for Account ID: {}", studentId);
                    return new NotFoundException("Student not found for the account");
                });

        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        if (questionCard.getStudent().getId().equals(studentId) && questionCard.getStatus().equals(QuestionCardStatus.PENDING)) {
            questionCard.setContent(dto.getContent());
            questionCard.setQuestionType(dto.getQuestionType());
            questionCard.setTitle(dto.getTitle());

            // Lưu thẻ câu hỏi vào cơ sở dữ liệu
            QuestionCard savedCard = questionCardRepository.save(questionCard);
            logger.info("QuestionCard updated with ID: {} for Student ID: {}", savedCard.getId(), student.getId());

            rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_Q_A, RealTimeQuestionDTO.builder()
                    .studentId(questionCard.getStudent().getId())
                    .counselorId(null)
                    .type(RealTimeQuestionDTO.Type.STUDENT_UPDATE)
                    .build());

            // Chuyển đổi entity sang DTO
            return QuestionCardMapper.toQuestionCardResponseDto(savedCard);
        } else {
            throw new ForbiddenException("You are not allow to delete this card");
        }
    }

    @Override
    public void flagQuestionCard(Long questionCardId, FlagQuestionCardRequestDTO dto) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        if(!questionCard.getStatus().equals(QuestionCardStatus.PENDING)) {
            throw new ForbiddenException("This card is not PENDING");
        }

        questionCard.setStatus(QuestionCardStatus.FLAGGED);
        questionCardRepository.save(questionCard);

        QuestionFlag questionFlag = new QuestionFlag();
        questionFlag.setQuestionCard(questionCard);
        questionCard.setReviewReason(dto.getReason());
        questionFlag.setStudent(questionCard.getStudent());

        questionFlagRepository.save(questionFlag);

        rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_Q_A, RealTimeQuestionDTO.builder()
                .studentId(questionCard.getStudent().getId())
                .counselorId(questionCard.getCounselor() != null ? questionCard.getCounselor().getId() : null)
                .type(RealTimeQuestionDTO.Type.FLAG)
                .build());

        notificationService.sendNotification(NotificationDTO.builder()
                .receiverId(questionCard.getStudent().getId())
                .message("Counselor has flag your question")
                .title("Counselor has flag your question")
                .sender("Counselor: " + questionCard.getCounselor().getFullName())
                .readStatus(false)
                .build());

        checkAndCreateBanIfNeeded(questionFlag.getStudent());
    }

    @Override
    public BanInformationResponseDTO getBanInformation(Long studentId) {
        // Tìm QuestionBan mới nhất cho sinh viên
        QuestionBan questionBan = questionBanRepository.findByStudentId(studentId)
                .orElse(null);

        BanInformationResponseDTO response = new BanInformationResponseDTO();
        if (questionBan != null) {
            response.setBan(true);
            response.setBanStartDate(questionBan.getBanStartDate());
            response.setBanEndDate(questionBan.getBanEndDate());

            // Lấy danh sách QuestionFlags liên quan đến QuestionBan này
            List<QuestionFlag> flags = questionBan.getQuestionFlags();
            List<BanInformationResponseDTO.QuestionFlagResponseDTO> flagResponses = flags.stream()
                    .map(flag -> BanInformationResponseDTO.QuestionFlagResponseDTO.builder()
                            .flagDate(flag.getFlagDate())
                            .reason(flag.getQuestionCard().getReviewReason())
                            .questionCard(QuestionCardMapper.toQuestionCardResponseDto(flag.getQuestionCard()))
                            .build())
                    .collect(Collectors.toList());

            response.setQuestionFlags(flagResponses);
        } else {
            response.setBan(false);
        }

        return response;
    }

    @Override
    public ChatSessionDTO getMessageByChatSessionForStudent(Long questionCardId, Long studentId) {
        QuestionCard questionCard = questionCardRepository.findByIdWithCounselor(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentId));

        if (!questionCard.getStudent().getId().equals(studentId)) {
            throw new ForbiddenException("You do not own this card");
        }

        return ChatSessionMapper.toChatSessionDTO(questionCard.getChatSession());
    }

    @Override
    public ChatSessionDTO getMessageByChatSessionForcounselor(Long questionCardId, Long counselorId) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found with ID: " + counselorId));

        if (!questionCard.getCounselor().getId().equals(counselorId)) {
            throw new ForbiddenException("You do not own this card");
        }

        return ChatSessionMapper.toChatSessionDTO(questionCard.getChatSession());
    }

    @Override
    public ChatSessionDTO getMessageByChatSession(Long questionCardId, Long id, Role role) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        if(role.equals(Role.STUDENT)) {
            if (!questionCard.getStudent().getId().equals(id)) {
                throw new ForbiddenException("You do not own this card");
            }
        } else {
            if (!questionCard.getCounselor().getId().equals(id)) {
                throw new ForbiddenException("You do not own this card");
            }
        }

        return ChatSessionMapper.toChatSessionDTO(questionCard.getChatSession());
    }

    @Override
    public void createChatSessionForQuestionCard(Long studentId, Long questionCardId) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        if (questionCard.getStudent().getId().equals(studentId) && !questionCard.isClosed()) {
            ChatSession chatSession = new ChatSession();
            chatSession.setQuestionCard(questionCard);

            chatSession.setCounselor(questionCard.getCounselor());
            chatSession.setStudent(questionCard.getStudent());
            chatSessionRepository.save(chatSession);
            questionCardRepository.save(questionCard);

            rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_Q_A, RealTimeQuestionDTO.builder()
                    .studentId(questionCard.getStudent().getId())
                    .counselorId(questionCard.getCounselor() != null ? questionCard.getCounselor().getId() : null)
                    .type(RealTimeQuestionDTO.Type.STUDENT_CREATE_CHAT_SESSION)
                    .build());

            Counselor counselor = questionCard.getCounselor();
            Student student = questionCard.getStudent();

            notificationService.sendNotification(NotificationDTO.builder()
                    .receiverId(counselor.getId())
                    .message("A student want to have a chat session with you")
                    .title("New chat session for Q and A is created")
                    .sender(String.format("Student: -%s-%s-", student.getStudentCode(), student.getFullName()))
                    .readStatus(false)
                    .build());
        } else {
            throw new ForbiddenException("You are not allow to answer this QC");
        }
    }

    @Override
    public List<QuestionCardResponseDTO> getAll(LocalDate from, LocalDate to) {
        LocalDateTime fromDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = (to != null) ? to.atTime(LocalTime.MAX) : null;

        List<QuestionCard> questionCards = questionCardRepository.findAllByCreatedDateBetween(fromDateTime, toDateTime);

        return questionCards.stream()
                .map(QuestionCardMapper::toQuestionCardResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaginationDTO<List<QuestionCardResponseDTO>> getPublicQuestionCardsWithFilterForStudent(QuestionCardFilterRequestDTO filterRequest, boolean isSuggestion) {
        logger.info("Fetching Question Cards with filters: {}", filterRequest);

        String query = filterRequest.getKeyword() == null ? "" : filterRequest.getKeyword();

        // Lấy vector embedding từ query
        List<Double> queryVectorDouble = openAIService.getEmbeddingFromOpenAPI(query);
        List<Float> queryVector = queryVectorDouble.stream()
                .map(Double::floatValue)
                .collect(Collectors.toList());

        Query filterQuery = Query.of(b -> b
                        .bool(bool -> {
                            if (filterRequest.getType() != null) {
                                bool.filter(f -> f.term(t -> t.field("questionType").value(filterRequest.getType().name())));
                            }

                            return bool;
                        })
        );

        System.out.println(filterQuery.toString());

        // Chuyển đổi queryVector sang JsonData
        JsonData queryVectorJson = JsonData.of(queryVector);

        // Tạo script_score để tính điểm tương tự dựa trên vector
        Query vectorQuery = null;

        if(query != null && query.isEmpty()) {
            vectorQuery = filterQuery;
        } else {
            vectorQuery =  Query.of(q -> q
                    .scriptScore(ss -> ss
                            .query(filterQuery) // Áp dụng các filter
                            .script(script -> script
                                    .inline(inline -> inline
                                            .source(
//                                                    "cosineSimilarity(params.queryVector, 'contentVector') + " +
                                                            "cosineSimilarity(params.queryVector, 'titleVector')"
                                            )
                                            .params(Map.of(
                                                    "queryVector", queryVectorJson // Dùng JsonData thay vì List<Float>
                                            ))
                                    )
                            )
                    )
            );
        }

        Query finalVectorQuery = vectorQuery;
        double minScoreThreshold = isSuggestion ? 0.85 : 0.75;
        SearchRequest searchRequest = SearchRequest.of(sr -> {
                    sr
                            .index("question_cards")
                            .query(finalVectorQuery)
                            .size(10000);
            if (query.isEmpty()) {
                // Thêm sắp xếp nếu query rỗng
                sr.sort(so -> so
                        .field(f -> f
                                .field("sortingId")
                                .order(SortOrder.Desc) // Sắp xếp theo thứ tự tăng dần
                        )
                );
            }

            if (!query.isEmpty()) {
                // Áp dụng min_score khi query không rỗng
                sr.minScore(minScoreThreshold);
            }
            return sr;
                }
        );

        SearchResponse<QuestionCardDocument> response;
        try {
            response = elasticsearchClient.search(searchRequest, QuestionCardDocument.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to search contribution question cards", e);
        }

        List<QuestionCardDocument> elasticResultsRaw = new ArrayList<>();
        for (Hit<QuestionCardDocument> hit : response.hits().hits()) {
            elasticResultsRaw.add(hit.source());
        }

//        elasticResultsRaw = getPage(elasticResultsRaw, filterRequest.getPagination().getPageNumber(), filterRequest.getPagination().getPageSize());

        Page<QuestionCard> questionCardsPage = new PageImpl<>(Collections.emptyList(), filterRequest.getPagination(), 0);

        questionCardsPage = questionCardRepository.findPublicQuestionCardsWithFilterForStudent(
                "",
                filterRequest.getStatus(),
                QuestionCard.PublicStatus.VISIBLE,
                filterRequest.getIsClosed(),
                filterRequest.getType(),
                PageRequest.of(0, 999999)
        );

        List<QuestionCard> joinValues = getJoinList(questionCardsPage.getContent(), elasticResultsRaw);

        List<QuestionCard> joinValuesPagination = getPage(joinValues, filterRequest.getPagination().getPageNumber(), filterRequest.getPagination().getPageSize());

        List<QuestionCardResponseDTO> questionCardDTOs = joinValuesPagination.stream()
                .map(QuestionCardMapper::toQuestionCardResponseDto)
                .collect(Collectors.toList());

        return PaginationDTO.<List<QuestionCardResponseDTO>>builder()
                .data(questionCardDTOs)
                .totalPages((joinValues.size() + filterRequest.getPagination().getPageSize() - 1) / filterRequest.getPagination().getPageSize())
                .totalElements(joinValues.size())
                .build();
    }

    @Override
    public void submitFeedback(Long questionCardId, AppointmentFeedbackDTO feedbackDTO, Long studentId) {
        // Check if the appointment exists
        QuestionCard questionCard = questionCardRepository.findById(questionCardId) // Use the new repository
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        // Check if the appointment status allows feedback
        if (!questionCard.isClosed()) {
            throw new BadRequestException("Feedback can only be given for closed");
        }

        if (questionCard.getFeedback() != null) {
            throw new BadRequestException("This question already had feedback");
        }

        // Create feedback entity
        QuestionCardFeedback feedback = QuestionCardFeedback.builder()
                .rating(feedbackDTO.getRating())
                .comment(feedbackDTO.getComment())
                .questionCard(questionCard)
                .counselor(questionCard.getCounselor())
                .build();

        questionCardFeedbackRepository.save(feedback);

        rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_Q_A, RealTimeAppointmentDTO.builder()
                .studentId(questionCard.getStudent().getId())
                .counselorId(questionCard.getCounselor().getId())
                .build());

        // Notify counselor
        notificationService.sendNotification(NotificationHelper.getNotificationFromStudentToCounselor(
                String.format("You have received new feedback from student -%s- for question %s.",
                        questionCard.getStudent().getFullName(), questionCard.getContent()),
                "New Feedback Received",
                false,
                questionCard.getStudent(),
                questionCard.getCounselor()
        ));
    }

    private List<QuestionCard> getJoinList(List<QuestionCard> questionCards, List<QuestionCardDocument> documents) {
        // Kiểm tra null hoặc danh sách rỗng
        if (questionCards == null || documents == null || documents.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> documentIdList = documents.stream().map(document -> Long.valueOf(document.getId())).toList();

        List<QuestionCard> returnValues = new ArrayList<>();

        for(Long documentId : documentIdList) {
            for(QuestionCard questionCard : questionCards) {
                if(questionCard.getId().equals(documentId)) {
                    returnValues.add(questionCard);
                }
            }
        }

        // Lọc danh sách Student dựa trên studentCode có trong joinedStudentCode
        return returnValues;
    }

    private List<QuestionCard> getPage(List<QuestionCard> list, int page, int size) {
        // Kiểm tra danh sách null hoặc trống
        if (list == null || list.isEmpty() || size <= 0 || page < 0) {
            return Collections.emptyList();
        }

        // Tính toán chỉ số bắt đầu và kết thúc
        int start = page * size;
        int end = Math.min(start + size, list.size());

        // Nếu chỉ số bắt đầu vượt quá kích thước danh sách, trả về danh sách rỗng
        if (start >= list.size()) {
            return Collections.emptyList();
        }

        // Trả về danh sách con
        return list.subList(start, end);
    }

    private void checkAndCreateBanIfNeeded(Student student) {
        // Lấy danh sách các cờ chưa gán với QuestionBan
        List<QuestionFlag> flags = questionFlagRepository.findByStudentAndQuestionBanIsNull(student);

        // Nếu có 3 cờ trở lên, tạo QuestionBan mới
        if (flags.size() >= 3) {
            QuestionBan questionBan = new QuestionBan();
            questionBan.setStudent(student);
            questionBan.setReason("Flagged for violating rules"); // Hoặc một lý do phù hợp
            questionBan.setBanStartDate(LocalDateTime.now());
            questionBan.setBanEndDate(LocalDateTime.now().plusDays(7)); // Khóa trong 7 ngày

            // Lưu QuestionBan vào cơ sở dữ liệu
            questionBanRepository.save(questionBan);

            // Gán QuestionBan vào các QuestionFlag
            for (QuestionFlag flag : flags) {
                flag.setQuestionBan(questionBan);
                questionFlagRepository.save(flag);
            }
        }
    }

    @Override
    public void acceptQuestionCard(Long questionCardId) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        if(questionCard.getAnswer() != null && !questionCard.getAnswer().isEmpty()) {
            questionCard.setAccepted(true);
            questionCardRepository.save(questionCard);
        } else {
            throw new ForbiddenException("Can not accept because of no answering yet");
        }

        rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_Q_A, RealTimeQuestionDTO.builder()
                .studentId(questionCard.getStudent().getId())
                .counselorId(questionCard.getCounselor() != null ? questionCard.getCounselor().getId() : null)
                .type(RealTimeQuestionDTO.Type.REVIEW)
                .build());

        notificationService.sendNotification(NotificationHelper.getNotificationFromStudentToCounselor(
                "Your answer is accepted",
                String.format("Your answer is accepted for question %s", questionCard.getTitle()),
                false,
                questionCard.getStudent(),
                questionCard.getCounselor()
        ));
    }

    @Override
    public long countOpenQuestionCardsByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));
        return questionCardRepository.countNotClosedQuestionCardsByStudent(student);
    }
}

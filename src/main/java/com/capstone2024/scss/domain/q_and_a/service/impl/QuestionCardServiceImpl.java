package com.capstone2024.scss.domain.q_and_a.service.impl;

import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.advice.exeptions.ForbiddenException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.notification.dtos.NotificationDTO;
import com.capstone2024.scss.application.q_and_a.dto.*;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.common.mapper.q_and_a.ChatSessionMapper;
import com.capstone2024.scss.domain.common.mapper.q_and_a.QuestionCardMapper;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.counselor.entities.NonAcademicCounselor;
import com.capstone2024.scss.domain.notification.services.NotificationService;
import com.capstone2024.scss.domain.q_and_a.entities.*;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionCardStatus;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionType;
import com.capstone2024.scss.domain.q_and_a.service.QuestionCardService;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.RabbitMQConfig;
import com.capstone2024.scss.infrastructure.repositories.StudentRepository;
import com.capstone2024.scss.infrastructure.repositories._and_a.*;
import com.capstone2024.scss.infrastructure.repositories.counselor.CounselorRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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

    @Override
    @Transactional
    public QuestionCardResponseDTO createQuestionCard(CreateQuestionCardRequestDTO dto, Long studentId) {
        logger.info("Starting creation of QuestionCard for Account ID: {}", studentId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student not found for Account ID: {}", studentId);
                    return new NotFoundException("Student not found for the account");
                });

        Topic topic = topicRepository.findById(dto.getTopicId())
                .orElseThrow(() -> {
                    logger.error("Topic not found for ID: {}", dto.getTopicId());
                    return new NotFoundException("Topic not found");
                });

        // Tạo thẻ câu hỏi mới
        QuestionCard questionCard = QuestionCard.builder()
                .content(dto.getContent())
                .questionType(dto.getQuestionType())
                .student(student)
                .status(QuestionCardStatus.PENDING) // Trạng thái mặc định
                .isTaken(false)
                .isClosed(false)
                .topic(topic)
                .build();

        // Lưu thẻ câu hỏi vào cơ sở dữ liệu
        QuestionCard savedCard = questionCardRepository.save(questionCard);
        logger.info("QuestionCard created with ID: {} for Student ID: {}", savedCard.getId(), student.getId());

        // Chuyển đổi entity sang DTO
        return QuestionCardMapper.toQuestionCardResponseDto(savedCard);
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
                filterRequest.getIsTaken(),
                filterRequest.getIsClosed(),
                filterRequest.getIsChatSessionClosed(),
                filterRequest.getType(),
                filterRequest.getTopicId(),
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

        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found with ID: " + counselorId));

        Page<QuestionCard> questionCardsPage = new PageImpl<>(Collections.emptyList(), filterRequest.getPagination(), 0);

        if(counselor instanceof NonAcademicCounselor) {
            questionCardsPage = questionCardRepository.findQuestionCardsWithFilterForCounselor(
                    filterRequest.getStudentCode(),
                    counselorId,
                    filterRequest.getKeyword(),
                    QuestionType.NON_ACADEMIC,
                    true,
                    filterRequest.getIsClosed(),
                    filterRequest.getIsChatSessionClosed(),
                    filterRequest.getTopicId(),
                    filterRequest.getPagination()
            );
        } else  {
            questionCardsPage = questionCardRepository.findQuestionCardsWithFilterForCounselor(
                    filterRequest.getStudentCode(),
                    counselorId,
                    filterRequest.getKeyword(),
                    QuestionType.ACADEMIC,
                    true,
                    filterRequest.getIsClosed(),
                    filterRequest.getIsChatSessionClosed(),
                    filterRequest.getTopicId(),
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

        if (questionCard.isTaken()) {
            throw new BadRequestException("Question card has already been taken.");
        }

        if (questionCard.isClosed()) {
            throw new BadRequestException("Question card is closed and cannot be taken.");
        }

        questionCard.setTaken(true);
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
                    false,
                    filterRequest.getTopicId(),
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
        message.setSentAt(LocalDateTime.now());
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
        if(questionCard.getStudent().getId().equals(studentId) && !questionCard.isTaken()) {
            questionCard.setClosed(true);
            questionCardRepository.save(questionCard);
        } else {
            throw new ForbiddenException("You are not allowed to close this QC");
        }
    }

    @Override
    public void answerQuestionCard(AnswerQuestionCardRequestDTO dto, Long counselorId, Long questionCardId) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        if (questionCard.isTaken() && questionCard.getCounselor().getId().equals(counselorId) && !questionCard.isClosed()) {
            questionCard.setAnswer(dto.getContent());
            questionCardRepository.save(questionCard);
        } else {
            throw new ForbiddenException("You are not allow to answer this QC");
        }
    }

    @Override
    public void editQuestionCard(AnswerQuestionCardRequestDTO dto, Long counselorId, Long questionCardId) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        if (questionCard.isTaken() && questionCard.getCounselor().getId().equals(counselorId) && !questionCard.isClosed()) {
            questionCard.setAnswer(dto.getContent());
            questionCardRepository.save(questionCard);
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
    public void reviewQuestionCard(Long questionCardId, QuestionCardStatus questionCardStatus) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        if(!questionCard.getStatus().equals(QuestionCardStatus.PENDING)) {
            throw new ForbiddenException("This card is not PENDING");
        }

        switch (questionCardStatus) {
            case VERIFIED -> {
                questionCard.setStatus(QuestionCardStatus.VERIFIED);
                questionCardRepository.save(questionCard);
            }
            case REJECTED -> {
                questionCard.setStatus(QuestionCardStatus.REJECTED);
                questionCardRepository.save(questionCard);
            }
            case FLAGGED -> {
                questionCard.setStatus(QuestionCardStatus.FLAGGED);
                questionCardRepository.save(questionCard);
            }
            default -> throw new BadRequestException("Invalid status");
        }
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
            questionCardRepository.save(questionCard);
            ChatSession chatSession = questionCard.getChatSession();
            chatSession.setClosed(true);
            chatSessionRepository.save(chatSession);
        } else {
            throw new ForbiddenException("You are not allowed to close this QC");
        }
    }

    @Override
    public void deleteQuestionCard(Long questionCardId, Long id) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        if (!questionCard.isTaken() && questionCard.getStudent().getId().equals(id)) {
            questionCardRepository.deleteById(questionCardId);
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

        if (!questionCard.isTaken() && questionCard.getStudent().getId().equals(studentId)) {
            questionCard.setContent(dto.getContent());
            questionCard.setQuestionType(dto.getQuestionType());

            // Lưu thẻ câu hỏi vào cơ sở dữ liệu
            QuestionCard savedCard = questionCardRepository.save(questionCard);
            logger.info("QuestionCard updated with ID: {} for Student ID: {}", savedCard.getId(), student.getId());

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
        questionFlag.setReason(dto.getReason());
        questionFlag.setStudent(questionCard.getStudent());

        questionFlagRepository.save(questionFlag);

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
                            .reason(flag.getReason())
                            .questionCard(QuestionCardMapper.toQuestionCardResponseDto(flag.getQuestionCard()))
                            .build())
                    .collect(Collectors.toList());

            response.setQuestionFlags(flagResponses);
        } else {
            response.setBan(false);
        }

        return response;
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
}

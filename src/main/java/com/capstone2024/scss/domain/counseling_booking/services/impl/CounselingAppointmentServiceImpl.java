package com.capstone2024.scss.domain.counseling_booking.services.impl;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.advice.exeptions.ForbiddenException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.enums.SlotStatus;
import com.capstone2024.scss.application.booking_counseling.dto.request.counceling_appointment.AppointmentFeedbackDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.counceling_appointment.OfflineAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.counceling_appointment.OnlineAppointmentRequestDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.counseling_appointment.dto.AppointmentReportResponse;
import com.capstone2024.scss.application.counseling_appointment.dto.request.appoinment_report.AppointmentReportRequest;
import com.capstone2024.scss.application.counseling_appointment.dto.request.counseling_appointment.AppointmentFilterDTO;
import com.capstone2024.scss.application.notification.dtos.NotificationDTO;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.*;
import com.capstone2024.scss.domain.common.utils.DateTimeUtil;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.AppointmentFeedback;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OfflineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OnlineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_report.entities.*;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.CounselingAppointmentRequestStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.notification.services.NotificationService;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentService;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.RabbitMQConfig;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto.RealTimeAppointmentDTO;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto.RealTimeCounselingSlotDTO;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.*;
import com.capstone2024.scss.infrastructure.repositories.counselor.CounselorRepository;
import com.capstone2024.scss.infrastructure.repositories.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CounselingAppointmentServiceImpl implements CounselingAppointmentService {
    private static final Logger logger = LoggerFactory.getLogger(CounselingAppointmentServiceImpl.class);
    private final CounselingAppointmentRequestRepository requestRepository;
    private final CounselingAppointmentRepository appointmentRepository;
    private final AppointmentFeedbackRepository appointmentFeedbackRepository;
    private final CounselorRepository counselorRepository;
    private final NotificationService notificationService;
    private final CounselingSlotRepository counselingSlotRepository;
    private final AppointmentReportRepository appointmentReportRepository;
    private final RabbitTemplate rabbitTemplate;
    private final StudentRepository studentRepository;

    @Transactional
    public void approveOnlineAppointment(Long requestId, Long counselorId, OnlineAppointmentRequestDTO dto) {
        logger.info("Approving online appointment for requestId: {} and counselorId: {}", requestId, counselorId);

        // Tìm yêu cầu từ requestId
        CounselingAppointmentRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    logger.error("Appointment request with id {} not found", requestId);
                    return new NotFoundException("Appointment request not found");
                });

        // Kiểm tra counselor có quyền phê duyệt yêu cầu này
        if (!request.getCounselor().getId().equals(counselorId)) {
            logger.warn("Counselor with id {} does not have permission to approve request {}", counselorId, requestId);
            throw new BadRequestException("You do not have permission to approve this appointment request");
        }

        // Kiểm tra trạng thái yêu cầu là WAITING
        if (request.getStatus() != CounselingAppointmentRequestStatus.WAITING) {
            logger.warn("Appointment request with id {} is not in WAITING status (current status: {})", requestId, request.getStatus());
            throw new BadRequestException("Appointment request is not in WAITING status and cannot be approved");
        }

        // Kiểm tra loại cuộc gặp là ONLINE
        if (request.getMeetingType() != MeetingType.ONLINE) {
            logger.warn("Appointment request with id {} is not of type ONLINE", requestId);
            throw new BadRequestException("Appointment is not of type ONLINE");
        }

        // Kiểm tra meetUrl hợp lệ
        if (dto.getMeetUrl() == null || dto.getMeetUrl().isEmpty()) {
            logger.warn("Meet URL is missing for requestId: {}", requestId);
            throw new BadRequestException("Meet URL is required for online appointments");
        }

        // Tạo CounselingAppointment Online
        OnlineAppointment appointment = OnlineAppointment.builder()
                .startDateTime(LocalDateTime.of(request.getRequireDate(), request.getStartTime()))
                .endDateTime(LocalDateTime.of(request.getRequireDate(), request.getEndTime()))
                .status(CounselingAppointmentStatus.WAITING)
                .appointmentRequest(request)
                .meetUrl(dto.getMeetUrl())
                .meetingType(request.getMeetingType())
                .student(request.getStudent())
                .counselor(request.getCounselor())
                .build();

        logger.info("Creating online appointment for requestId: {} with meet URL: {}", requestId, dto.getMeetUrl());

        // Cập nhật trạng thái yêu cầu
        request.setStatus(CounselingAppointmentRequestStatus.APPROVED);
        logger.info("Appointment request with id {} has been approved", requestId);

        // Lưu yêu cầu và cuộc hẹn
        appointmentRepository.save(appointment);
        logger.info("Online appointment for requestId: {} has been saved", requestId);

        requestRepository.save(request);
        logger.info("Appointment request with id {} has been updated and saved", requestId);

        Optional<CounselingSlot> slot = counselingSlotRepository.findByStartTimeAndEndTime(appointment.getStartDateTime().toLocalTime(), appointment.getEndDateTime().toLocalTime());

        slot.ifPresent(counselingSlot -> rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_COUNSELING_SLOT, RealTimeCounselingSlotDTO.builder()
                .counselorId(appointment.getAppointmentRequest().getCounselor().getId())
                .slotId(counselingSlot.getId())
                .dateChange(appointment.getStartDateTime().toLocalDate())
                .newStatus(SlotStatus.UNAVAILABLE)
                .studentId(appointment.getAppointmentRequest().getStudent().getId())
                .build()));

        rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_COUNSELING_APPOINTMENT, RealTimeAppointmentDTO.builder()
                .studentId(appointment.getAppointmentRequest().getStudent().getId())
                .counselorId(appointment.getAppointmentRequest().getCounselor().getId())
                .build());

        sendOnlineAppointmentApprovalNotification(request, appointment);
    }

    private void sendOnlineAppointmentApprovalNotification(CounselingAppointmentRequest request, OnlineAppointment appointment) {
        Counselor counselor = request.getCounselor();
        Student student = request.getStudent();

        // Format date and time using DateTimeUtil
        String formattedStart = DateTimeUtil.formatDateTime(appointment.getStartDateTime());
        String formattedEnd = DateTimeUtil.formatDateTime(appointment.getEndDateTime());

        // Notify counselor
        notificationService.sendNotification(NotificationDTO.builder()
                .receiverId(counselor.getId())
                .message(String.format("Your counseling appointment request for student -%s- (%s) has been approved.\nAppointment Time: %s to %s\nMeet URL: %s",
                        student.getFullName(), student.getStudentCode(), formattedStart, formattedEnd, appointment.getMeetUrl()))
                .title("Online Appointment Approved")
                .sender("Counseling System")
                .readStatus(false)
                .build());

        // Notify student
        notificationService.sendNotification(NotificationDTO.builder()
                .receiverId(student.getId())
                .message(String.format("Your counseling appointment with counselor -%s- has been approved.\nAppointment Time: %s to %s\nMeet URL: %s",
                        counselor.getFullName(), formattedStart, formattedEnd, appointment.getMeetUrl()))
                .title("Online Appointment Approved")
                .sender("Counselor: " + counselor.getFullName())
                .readStatus(false)
                .build());

        logger.info("Notifications sent to counselorId: {} and studentId: {} for appointmentId: {}", counselor.getId(), student.getId(), appointment.getId());
    }

    @Transactional
    public void approveOfflineAppointment(Long requestId, Long counselorId, OfflineAppointmentRequestDTO dto) {
        logger.info("Approving offline appointment for requestId: {} by counselorId: {}", requestId, counselorId);

        CounselingAppointmentRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    logger.error("Appointment request not found for id: {}", requestId);
                    return new NotFoundException("Appointment request not found");
                });

        if (!request.getCounselor().getId().equals(counselorId)) {
            logger.warn("Unauthorized attempt to approve appointment request by counselorId: {}", counselorId);
            throw new BadRequestException("You do not have permission to approve this appointment request");
        }

        if (request.getStatus() != CounselingAppointmentRequestStatus.WAITING) {
            logger.warn("Appointment request id: {} is not in WAITING status", requestId);
            throw new BadRequestException("Appointment request is not in WAITING status and cannot be approved");
        }

        if (request.getMeetingType() != MeetingType.OFFLINE) {
            logger.warn("Appointment request id: {} is not of type OFFLINE", requestId);
            throw new BadRequestException("Appointment is not of type OFFLINE");
        }

        if (dto.getAddress() == null || dto.getAddress().isEmpty()) {
            logger.warn("Invalid address provided for appointment request id: {}", requestId);
            throw new BadRequestException("Address is required for offline appointments");
        }

        logger.debug("Creating offline appointment for requestId: {}", requestId);
        OfflineAppointment appointment = OfflineAppointment.builder()
                .startDateTime(LocalDateTime.of(request.getRequireDate(), request.getStartTime()))
                .endDateTime(LocalDateTime.of(request.getRequireDate(), request.getEndTime()))
                .status(CounselingAppointmentStatus.WAITING)
                .appointmentRequest(request)
                .address(dto.getAddress())
                .meetingType(request.getMeetingType())
                .student(request.getStudent())
                .counselor(request.getCounselor())
                .build();

        request.setStatus(CounselingAppointmentRequestStatus.APPROVED);

        OfflineAppointment appointmentPersist = appointmentRepository.save(appointment);
        requestRepository.save(request);

        Optional<CounselingSlot> slot = counselingSlotRepository.findByStartTimeAndEndTime(appointment.getStartDateTime().toLocalTime(), appointment.getEndDateTime().toLocalTime());

        slot.ifPresent(counselingSlot -> rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_COUNSELING_SLOT, RealTimeCounselingSlotDTO.builder()
                .counselorId(appointment.getAppointmentRequest().getCounselor().getId())
                .slotId(counselingSlot.getId())
                .dateChange(appointment.getStartDateTime().toLocalDate())
                .newStatus(SlotStatus.UNAVAILABLE)
                .studentId(appointment.getAppointmentRequest().getStudent().getId())
                .build()));

        rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_COUNSELING_APPOINTMENT, RealTimeAppointmentDTO.builder()
                .studentId(appointment.getAppointmentRequest().getStudent().getId())
                .counselorId(appointment.getAppointmentRequest().getCounselor().getId())
                .build());

        logger.info("Successfully approved offline appointment for requestId: {}", requestId);

        sendOfflineAppointmentApprovalNotification(request, appointmentPersist);
    }

    private void sendOfflineAppointmentApprovalNotification(CounselingAppointmentRequest request, OfflineAppointment appointment) {
        Counselor counselor = request.getCounselor();
        Student student = request.getStudent();

        // Format date and time using DateTimeUtil
        String formattedStart = DateTimeUtil.formatDateTime(appointment.getStartDateTime());
        String formattedEnd = DateTimeUtil.formatDateTime(appointment.getEndDateTime());

        // Notify counselor
        notificationService.sendNotification(NotificationDTO.builder()
                .receiverId(counselor.getId())
                .message(String.format("Your counseling appointment request for student -%s- (%s) has been approved.\nAppointment Time: %s to %s\nLocation: %s",
                        student.getFullName(), student.getStudentCode(), formattedStart, formattedEnd, appointment.getAddress()))
                .title("Offline Appointment Approved")
                .sender("Counseling System")
                .readStatus(false)
                .build());

        // Notify student
        notificationService.sendNotification(NotificationDTO.builder()
                .receiverId(student.getId())
                .message(String.format("Your counseling appointment with counselor -%s- has been approved.\nAppointment Time: %s to %s\nLocation: %s",
                        counselor.getFullName(), formattedStart, formattedEnd, appointment.getAddress()))
                .title("Offline Appointment Approved")
                .sender("Counselor: " + counselor.getFullName())
                .readStatus(false)
                .build());

        logger.info("Notifications sent to counselorId: {} and studentId: {} for appointmentId: {}", counselor.getId(), student.getId(), appointment.getId());
    }

    @Transactional
    public void denyAppointmentRequest(Long requestId, Long counselorId) {
        logger.info("Denying appointment request for requestId: {} by counselorId: {}", requestId, counselorId);

        CounselingAppointmentRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    logger.error("Appointment request not found for id: {}", requestId);
                    return new NotFoundException("Appointment request not found");
                });

        if (!request.getCounselor().getId().equals(counselorId)) {
            logger.warn("Unauthorized attempt to deny appointment request by counselorId: {}", counselorId);
            throw new BadRequestException("Counselor not authorized to deny this request");
        }

        if (request.getStatus() != CounselingAppointmentRequestStatus.WAITING) {
            logger.warn("Appointment request id: {} is not in WAITING status", requestId);
            throw new BadRequestException("Only requests with status WAITING can be denied");
        }

        request.setStatus(CounselingAppointmentRequestStatus.DENIED);
        requestRepository.save(request);

        logger.info("Successfully denied appointment request for requestId: {}", requestId);

        Optional<CounselingSlot> slot = counselingSlotRepository.findByStartTimeAndEndTime(request.getStartTime(), request.getEndTime());

        slot.ifPresent((counselingSlot) -> rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_COUNSELING_SLOT, RealTimeCounselingSlotDTO.builder()
                .counselorId(request.getCounselor().getId())
                .slotId(counselingSlot.getId())
                .dateChange(request.getRequireDate())
                .newStatus(SlotStatus.AVAILABLE)
                .build()));

        rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_COUNSELING_APPOINTMENT, RealTimeAppointmentDTO.builder()
                .studentId(request.getStudent().getId())
                .counselorId(request.getCounselor().getId())
                .build());

        sendAppointmentDenialNotification(request);
    }

    private void sendAppointmentDenialNotification(CounselingAppointmentRequest request) {
        Counselor counselor = request.getCounselor();
        Student student = request.getStudent();
        LocalDate appointmentDate = request.getRequireDate();
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime();

        // Format the date and time for the notification message
        String formattedDate = appointmentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String formattedStartTime = startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        String formattedEndTime = endTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        // Notify counselor
        notificationService.sendNotification(NotificationDTO.builder()
                .receiverId(counselor.getId())
                .message(String.format("You have successfully denied the counseling appointment request for student -%s- (%s) on %s from %s to %s.",
                        student.getFullName(), student.getStudentCode(), formattedDate, formattedStartTime, formattedEndTime))
                .title("Appointment Request Denied")
                .sender("Counseling System")
                .readStatus(false)
                .build());

        // Notify student
        notificationService.sendNotification(NotificationDTO.builder()
                .receiverId(student.getId())
                .message(String.format("Your counseling appointment request with counselor -%s- has been denied. The requested date was %s, from %s to %s.",
                        counselor.getFullName(), formattedDate, formattedStartTime, formattedEndTime))
                .title("Appointment Request Denied")
                .sender("Counselor: " + counselor.getFullName())
                .readStatus(false)
                .build());

        logger.info("Denial notifications with date and time slot sent to counselorId: {} and studentId: {} for requestId: {}", counselor.getId(), student.getId(), request.getId());
    }

    ///////////////////////////////////////////////////////////////////////////

    public List<CounselingAppointmentDTO> getAppointmentsForCounselor(LocalDate fromDate, LocalDate toDate, Long counselorId) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

        List<CounselingAppointment> appointments = appointmentRepository.findAllByCounselorIdAndDateRange(counselorId, fromDateTime, toDateTime);

        return appointments.stream()
                .map(CounselingAppointmentMapper::toCounselingAppointmentDTO)
                .collect(Collectors.toList());
    }

    public List<CounselingAppointmentDTO> getAppointmentsForStudent(LocalDate fromDate, LocalDate toDate, Long studentId) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

        List<CounselingAppointment> appointments = appointmentRepository.findAllByStudentIdAndDateRange(studentId, fromDateTime, toDateTime);

        return appointments.stream()
                .map(CounselingAppointmentMapper::toCounselingAppointmentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void submitFeedback(Long appointmentId, AppointmentFeedbackDTO feedbackDTO, Long studentId) {
        // Check if the appointment exists
        CounselingAppointment appointment = appointmentRepository.findById(appointmentId) // Use the new repository
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        // Check if the appointment status allows feedback
        if (appointment.getStatus() != CounselingAppointmentStatus.ATTEND) {
            throw new BadRequestException("Feedback can only be given for attended appointments");
        }

        if (appointment.getFeedback() != null) {
            throw new BadRequestException("This appointment already had feedback");
        }

        if (!appointment.getAppointmentRequest().getStudent().getId().equals(studentId)) {
            throw new ForbiddenException("You are not allowed to feedback this appointment");
        }

        // Update counselor rating
        Counselor counselor = appointment.getCounselor(); // Assuming Counselor is linked to Appointment
        counselor.setRating(calculateNewRating(counselor, feedbackDTO.getRating()));
        counselorRepository.save(counselor);

        // Create feedback entity
        AppointmentFeedback feedback = AppointmentFeedback.builder()
                .rating(feedbackDTO.getRating())
                .comment(feedbackDTO.getComment())
                .appointment(appointment)
                .counselor(counselor)
                .build();

        appointmentFeedbackRepository.save(feedback);

        rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_COUNSELING_APPOINTMENT, RealTimeAppointmentDTO.builder()
                .studentId(appointment.getAppointmentRequest().getStudent().getId())
                .counselorId(appointment.getAppointmentRequest().getCounselor().getId())
                .build());

        // Send notifications
        sendFeedbackNotification(counselor, appointment, feedbackDTO, studentId);
    }

    private void sendFeedbackNotification(Counselor counselor, CounselingAppointment appointment, AppointmentFeedbackDTO feedbackDTO, Long studentId) {
        Student student = appointment.getAppointmentRequest().getStudent();
        String studentFullName = student.getFullName();

        // Notify counselor
        notificationService.sendNotification(NotificationDTO.builder()
                .receiverId(counselor.getId())
                .message(String.format("You have received new feedback from student -%s- for appointment on %s.",
                        studentFullName, appointment.getStartDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))))
                .title("New Feedback Received")
                .sender("Student: " + student.getFullName() + "-" + student.getStudentCode())
                .readStatus(false)
                .build());

        // Notify student
        notificationService.sendNotification(NotificationDTO.builder()
                .receiverId(student.getId())
                .message("Thank you for your feedback on the appointment. Your input helps us improve our services!")
                .title("Feedback Submitted")
                .sender("Counseling System")
                .readStatus(false)
                .build());

        logger.info("Feedback notification sent to counselorId: {} and studentId: {} for appointmentId: {}", counselor.getId(), student.getId(), appointment.getId());
    }

    @Override
    public void takeAttendanceForAppointment(Long appointmentId, CounselingAppointmentStatus counselingAppointmentStatus, Long counselorId) {
        // Check if the appointment exists
        CounselingAppointment appointment = appointmentRepository.findById(appointmentId) // Use the new repository
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        if (!appointment.getAppointmentRequest().getCounselor().getId().equals(counselorId)) {
            throw new ForbiddenException("You have no permission for this appointment");
        }

        if (counselingAppointmentStatus.equals(CounselingAppointmentStatus.ATTEND) || counselingAppointmentStatus.equals(CounselingAppointmentStatus.ABSENT)) {
            appointment.setStatus(counselingAppointmentStatus);
            CounselingAppointment counselingAppointment = appointmentRepository.save(appointment);

            sendAttendanceNotification(appointment, counselingAppointmentStatus);

            rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_COUNSELING_APPOINTMENT, RealTimeAppointmentDTO.builder()
                            .studentId(appointment.getAppointmentRequest().getStudent().getId())
                            .counselorId(appointment.getAppointmentRequest().getCounselor().getId())
                    .build());
        } else {
            throw new BadRequestException("Invalid parameter, only ATTEND or ABSENT");
        }
    }

    @Override
    public PaginationDTO<List<CounselingAppointmentDTO>> getAppointmentsWithFilterForCounselor(AppointmentFilterDTO filterDTO, Counselor counselor) {
        Pageable pageable = createPageable(filterDTO);

        LocalDateTime fromDateTime = filterDTO.getFromDate() != null ? filterDTO.getFromDate().atStartOfDay() : null;
        LocalDateTime toDateTime = filterDTO.getToDate() != null ? filterDTO.getToDate().atTime(LocalTime.MAX) : null;

        Page<CounselingAppointment> appointmentsPage = appointmentRepository.findAppointmentsForCounselorWithFilter(
                filterDTO.getStudentCode(),
                fromDateTime,
                toDateTime,
                filterDTO.getStatus(),
                counselor,
                pageable);

        List<CounselingAppointmentDTO> appointmentDTOs = appointmentsPage.getContent()
                .stream()
                .map(CounselingAppointmentMapper::toCounselingAppointmentDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<CounselingAppointmentDTO>>builder()
                .data(appointmentDTOs)
                .totalPages(appointmentsPage.getTotalPages())
                .totalElements((int) appointmentsPage.getTotalElements())
                .build();
    }

    @Override
    public PaginationDTO<List<CounselingAppointmentDTO>> getAppointmentsWithFilterForStudent(AppointmentFilterDTO filterDTO, Student student) {
        Pageable pageable = createPageable(filterDTO);

        LocalDateTime fromDateTime = filterDTO.getFromDate() != null ? filterDTO.getFromDate().atStartOfDay() : null;
        LocalDateTime toDateTime = filterDTO.getToDate() != null ? filterDTO.getToDate().atTime(LocalTime.MAX) : null;

        Page<CounselingAppointment> appointmentsPage = appointmentRepository.findAppointmentsForStudentWithFilter(
                fromDateTime,
                toDateTime,
                filterDTO.getStatus(),
                student,
                pageable);

        List<CounselingAppointmentDTO> appointmentDTOs = appointmentsPage.getContent()
                .stream()
                .map(CounselingAppointmentMapper::toCounselingAppointmentDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<CounselingAppointmentDTO>>builder()
                .data(appointmentDTOs)
                .totalPages(appointmentsPage.getTotalPages())
                .totalElements((int) appointmentsPage.getTotalElements())
                .build();
    }

    @Override
    public PaginationDTO<List<CounselingAppointmentDTO>> getAppointmentsWithFilterForStudent(AppointmentFilterDTO filterDTO, Long studentId) {
        Pageable pageable = createPageable(filterDTO);

        Student student = studentRepository
                .findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student with ID:" + studentId + " not found"));

        LocalDateTime fromDateTime = filterDTO.getFromDate() != null ? filterDTO.getFromDate().atStartOfDay() : null;
        LocalDateTime toDateTime = filterDTO.getToDate() != null ? filterDTO.getToDate().atTime(LocalTime.MAX) : null;

        Page<CounselingAppointment> appointmentsPage = appointmentRepository.findAppointmentsForStudentWithFilter(
                fromDateTime,
                toDateTime,
                filterDTO.getStatus(),
                student,
                pageable);

        List<CounselingAppointmentDTO> appointmentDTOs = appointmentsPage.getContent()
                .stream()
                .map(CounselingAppointmentMapper::toCounselingAppointmentDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<CounselingAppointmentDTO>>builder()
                .data(appointmentDTOs)
                .totalPages(appointmentsPage.getTotalPages())
                .totalElements((int) appointmentsPage.getTotalElements())
                .build();
    }

    @Override
    public AppointmentReportResponse createAppointmentReport(AppointmentReportRequest request, Long appointmentId, Counselor counselor) {
        CounselingAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + appointmentId));

        if(appointment.getReport() != null) {
            throw new BadRequestException("This appoinment already had report");
        }
        // Build the main entity
        AppointmentReport appointmentReport = AppointmentReport.builder()
                .student(appointment.getAppointmentRequest().getStudent())
                .counselor(counselor)
                .counselingAppointment(appointment)
                .specificGoal(request.getConsultationGoal().getSpecificGoal())
                .reason(request.getConsultationGoal().getReason())
                .summaryOfDiscussion(request.getConsultationContent().getSummaryOfDiscussion())
                .mainIssues(request.getConsultationContent().getMainIssues())
                .studentEmotions(request.getConsultationContent().getStudentEmotions())
                .studentReactions(request.getConsultationContent().getStudentReactions())
                .counselorConclusion(request.getConsultationConclusion().getCounselorConclusion())
                .followUpNeeded(request.getConsultationConclusion().isFollowUpNeeded())
                .followUpNotes(request.getConsultationConclusion().getFollowUpNotes())
                .interventionType(request.getIntervention().getType())
                .interventionDescription(request.getIntervention().getDescription())

                .build();

        // Save the report
        AppointmentReport savedReport = appointmentReportRepository.save(appointmentReport);

        return AppointmentReportMapper.toAppointmentReportResponse(savedReport);
    }

    @Override
    public AppointmentReportResponse getAppointmentReportByAppointmentId(Long appointmentId, Counselor counselor) {
        // Kiểm tra xem Appointment có thuộc về Counselor không
        CounselingAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        // Kiểm tra Counselor có phải là người phụ trách Appointment này không
        if (!appointment.getAppointmentRequest().getCounselor().getId().equals(counselor.getId())) {
            throw new ForbiddenException("Counselor does not have access to this appointment report");
        }

        // Lấy AppointmentReport tương ứng
        AppointmentReport report = appointment.getReport();

        // Map sang DTO để trả về
        return AppointmentReportMapper.toAppointmentReportResponse(report);
    }

    @Override
    public CounselingAppointmentDTO getOneAppointment(Long appointmentId) {
        CounselingAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));
        return CounselingAppointmentMapper.toCounselingAppointmentDTO(appointment);
    }

    private Pageable createPageable(AppointmentFilterDTO filterDTO) {
        Sort sort = Sort.by(filterDTO.getSortBy());
        sort = filterDTO.getSortDirection() == SortDirection.ASC ? sort.ascending() : sort.descending();
        return PageRequest.of(filterDTO.getPage() - 1, 10, sort);
    }

    private void sendAttendanceNotification(CounselingAppointment appointment, CounselingAppointmentStatus status) {
        Counselor counselor = appointment.getAppointmentRequest().getCounselor();
        Student student = appointment.getAppointmentRequest().getStudent();

        String appointmentDateTime = appointment.getStartDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

        // Notify counselor
        notificationService.sendNotification(NotificationDTO.builder()
                .receiverId(counselor.getId())
                .message(String.format("You have marked the appointment on %s as %s.", appointmentDateTime, status))
                .title("Appointment Attendance Updated")
                .sender("Counseling System")
                .readStatus(false)
                .build());

        // Notify student
        notificationService.sendNotification(NotificationDTO.builder()
                .receiverId(student.getId())
                .message(String.format("Your appointment scheduled on %s has been marked as %s.", appointmentDateTime, status))
                .title("Appointment Attendance Notification")
                .sender("Counselor: " + counselor.getFullName())
                .readStatus(false)
                .build());

        logger.info("Attendance notification sent: CounselorId: {}, StudentId: {}, AppointmentId: {}, Status: {}",
                counselor.getId(), student.getId(), appointment.getId(), status);
    }

    private BigDecimal calculateNewRating(Counselor counselor, BigDecimal newRating) {
        // Fetch existing ratings for the counselor
        List<AppointmentFeedback> feedbackList = counselor.getFeedbackList();

        // Calculate the new average rating
        BigDecimal totalRating = BigDecimal.ZERO;
        int feedbackCount = feedbackList.size();

        // Sum the existing ratings
        for (AppointmentFeedback feedback : feedbackList) {
            totalRating = totalRating.add(feedback.getRating());
        }

        // Add the new rating to the total
        totalRating = totalRating.add(newRating);
        feedbackCount++; // Increase the count to include the new rating

        // Calculate the average and keep one decimal place
        return feedbackCount > 0
                ? totalRating.divide(BigDecimal.valueOf(feedbackCount), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(1); // Ensure it returns a BigDecimal with one decimal place
    }
}

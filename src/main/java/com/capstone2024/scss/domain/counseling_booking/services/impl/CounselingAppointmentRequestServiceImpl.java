package com.capstone2024.scss.domain.counseling_booking.services.impl;

import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.advice.exeptions.ForbiddenException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.booking_counseling.dto.SlotDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.AppointmentDetailsDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselingAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselorAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.StudentAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.enums.SlotStatus;
import com.capstone2024.scss.application.booking_counseling.dto.request.AppointmentRequestFilterDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.UpdateAppointmentRequestDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.notification.dtos.NotificationDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.common.mapper.account.ProfileMapper;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.CounselorProfileMapper;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.StudentProfileMapper;
import com.capstone2024.scss.domain.common.utils.DateTimeUtil;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OfflineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OnlineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.CounselingAppointmentRequestStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.notification.services.NotificationService;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentRequestService;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.RabbitMQConfig;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto.RealTimeCounselingSlotDTO;
import com.capstone2024.scss.infrastructure.repositories.CounselingAppointmentRepository;
import com.capstone2024.scss.infrastructure.repositories.CounselingAppointmentRequestRepository;
import com.capstone2024.scss.infrastructure.repositories.CounselingSlotRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.CounselorRepository;
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
import java.time.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CounselingAppointmentRequestServiceImpl implements CounselingAppointmentRequestService {

    private static final Logger logger = LoggerFactory.getLogger(CounselingAppointmentRequestService.class);
    private final CounselingAppointmentRequestRepository requestRepository;
    private final CounselingSlotRepository slotRepository;
    private final CounselorRepository counselorRepository;
    private final CounselingAppointmentRepository counselingAppointmentRepository;
    private final RabbitTemplate rabbitTemplate;
    private final NotificationService notificationService;

    public CounselingAppointmentRequestServiceImpl(CounselingAppointmentRequestRepository requestRepository, CounselingSlotRepository slotRepository, CounselorRepository counselorRepository, CounselingAppointmentRepository counselingAppointmentRepository, RabbitTemplate rabbitTemplate, NotificationService notificationService) {
        this.requestRepository = requestRepository;
        this.slotRepository = slotRepository;
        this.counselorRepository = counselorRepository;
        this.counselingAppointmentRepository = counselingAppointmentRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.notificationService = notificationService;
    }

    @Override
    public Map<LocalDate, List<SlotDTO>> getDailySlots(Long counselorId, LocalDate from, LocalDate to, Long studentId) {
        logger.info("Fetching daily slots for counselorId: {}, from: {}, to: {}", counselorId, from, to);
        // Find the counselor
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found"));
        List<CounselingAppointmentRequest> requests = requestRepository.findByCounselorIdAndRequireDateBetween(counselorId, from, to);
        List<CounselingSlot> slots = slotRepository.findAllSlots();
        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime();

        Map<LocalDate, List<SlotDTO>> dailySlots = new LinkedHashMap<>();
        LocalDate currentDate = from;

        while (!currentDate.isAfter(to)) {
            // Kiểm tra nếu ngày hiện tại là thứ Bảy hoặc Chủ Nhật
            if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                dailySlots.put(currentDate, Collections.emptyList()); // Trả về mảng rỗng
            } else {
                final LocalDate dateToCheck = currentDate;
                List<SlotDTO> slotDTOs = slots.stream()
                        .map(slot -> {
                            boolean isSlotTaken = requests.stream()
                                    .anyMatch(r -> r.getRequireDate().equals(dateToCheck) &&
                                            (r.getStatus() == CounselingAppointmentRequestStatus.WAITING ||
                                                    r.getStatus() == CounselingAppointmentRequestStatus.APPROVED) &&
                                            (r.getStartTime().isBefore(slot.getEndTime()) &&
                                                    r.getEndTime().isAfter(slot.getStartTime())));

                            boolean isMyAppointment = requests.stream()
                                    .anyMatch(r -> r.getRequireDate().equals(dateToCheck) &&
                                            (r.getStatus() == CounselingAppointmentRequestStatus.WAITING ||
                                                    r.getStatus() == CounselingAppointmentRequestStatus.APPROVED) &&
                                            r.getStudent() != null &&
                                            r.getStudent().getId().equals(studentId) &&
                                            (r.getStartTime().isBefore(slot.getEndTime()) &&
                                                    r.getEndTime().isAfter(slot.getStartTime())));

                            SlotStatus status;
                            if (isSlotTaken) {
                                status = SlotStatus.UNAVAILABLE;
                            } else if (LocalDateTime.of(dateToCheck, slot.getEndTime()).isBefore(now)) {
                                status = SlotStatus.EXPIRED;
                            } else {
                                status = SlotStatus.AVAILABLE;
                            }

                            return SlotDTO.builder()
                                    .slotId(slot.getId())
                                    .slotCode(slot.getSlotCode())
                                    .startTime(slot.getStartTime())
                                    .endTime(slot.getEndTime())
                                    .status(status)
                                    .isMyAppointment(isMyAppointment)
                                    .build();
                        })
                        .collect(Collectors.toList());

                dailySlots.put(currentDate, slotDTOs);
            }
            currentDate = currentDate.plusDays(1);
        }

        logger.info("Daily slots fetched successfully.");
        return dailySlots;
    }

    @Override
    public CounselingAppointmentRequest createAppointmentRequest(String slotCode, LocalDate date, Long counselorId, boolean isOnline, String reason, Student student) {
        logger.info("Creating appointment request for slotCode: {}, date: {}, counselorId: {}, isOnline: {}, reason: {}",
                slotCode, date, counselorId, isOnline, reason);

        // Find the slot
        CounselingSlot slot = slotRepository.findBySlotCode(slotCode)
                .orElseThrow(() -> new NotFoundException("Slot not found"));

        // Validate slot status
        SlotStatus slotStatus = getSlotStatus(slot, date);
        if (slotStatus != SlotStatus.AVAILABLE) {
            throw new BadRequestException("Slot is not available");
        }

        // Find the counselor
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found"));

        // Create the appointment request
        CounselingAppointmentRequest appointmentRequest = CounselingAppointmentRequest.builder()
                .requireDate(date)
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .status(CounselingAppointmentRequestStatus.WAITING)
                .meetingType(isOnline ? MeetingType.ONLINE : MeetingType.OFFLINE)
                .reason(reason)
                .counselor(counselor)
                .student(student)
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_COUNSELING_SLOT, RealTimeCounselingSlotDTO.builder()
                        .counselorId(counselor.getId())
                        .slotId(slot.getId())
                        .dateChange(date)
                        .studentId(appointmentRequest.getStudent().getId())
                        .newStatus(SlotStatus.UNAVAILABLE)
                .build());

        notificationService.sendNotification(NotificationDTO.builder()
                .receiverId(counselorId)
                .message("Student named -" + student.getFullName() + "-" + student.getStudentCode() + "- has sent you a counseling appointment request")
                .title("Counseling appointment request from student")
                .sender("Student: " + student.getFullName() + "-" + student.getStudentCode())
                .readStatus(false)
                .build());

        // Save the appointment request
        return requestRepository.save(appointmentRequest);
    }

    private SlotStatus getSlotStatus(CounselingSlot slot, LocalDate date) {
        // Assume we have a method to get appointment requests for a slot on a specific date
        boolean isSlotTaken = requestRepository.findByRequireDateAndStartTimeAndEndTime(date, slot.getStartTime(), slot.getEndTime()).stream()
                .anyMatch(r -> r.getStatus() == CounselingAppointmentRequestStatus.WAITING ||
                        r.getStatus() == CounselingAppointmentRequestStatus.APPROVED);

        LocalDateTime now = LocalDateTime.now();
        if (isSlotTaken) {
            return SlotStatus.UNAVAILABLE;
        } else if (LocalDateTime.of(date, slot.getEndTime()).isBefore(now)) {
            return SlotStatus.EXPIRED;
        } else {
            return SlotStatus.AVAILABLE;
        }
    }

    public PaginationDTO<List<CounselingAppointmentRequestDTO>> getAppointmentsRequest(Account principle, AppointmentRequestFilterDTO filterRequest) {
        Sort sort = Sort.by(filterRequest.getSortBy());
        sort = filterRequest.getSortDirection() == SortDirection.ASC ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(
                filterRequest.getPagination().getPageNumber(),
                filterRequest.getPagination().getPageSize(),
                sort
        );
        Page<CounselingAppointmentRequest> page = switch (principle.getRole()) {
            case Role.STUDENT -> {
                Student student = (Student) principle.getProfile();
                yield requestRepository.findByStudentIdAndFilters(
                        student.getId(),
                        filterRequest.getDateFrom(),
                        filterRequest.getDateTo(),
                        filterRequest.getMeetingType() != null ? filterRequest.getMeetingType() : null,
                        pageable
                );
            }
            case Role.COUNSELOR -> {
                Counselor counselor = (Counselor) principle.getProfile();
                yield requestRepository.findByCounselorIdAndFilters(
                        counselor.getId(),
                        filterRequest.getDateFrom(),
                        filterRequest.getDateTo(),
                        filterRequest.getMeetingType() != null ? filterRequest.getMeetingType() : null,
                        pageable
                );
            }
            default -> throw new BadRequestException("Invalid role specified");
        };

        List<CounselingAppointmentRequestDTO> appointmentDTOs = page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<CounselingAppointmentRequestDTO>>builder()
                .data(appointmentDTOs)
                .totalPages(page.getTotalPages())
                .totalElements((int) page.getTotalElements())
                .build();
    }

    private CounselingAppointmentRequestDTO convertToDTO(CounselingAppointmentRequest request) {
        List<CounselingAppointment> appointments = request.getCounselingAppointments();
        AppointmentDetailsDTO appointmentDetails = null;
        if(appointments != null && !appointments.isEmpty()) {
            CounselingAppointment appointment = appointments.getLast();

            if (appointment instanceof OnlineAppointment onlineAppointment) {
                appointmentDetails = AppointmentDetailsDTO.builder()
                        .meetUrl(onlineAppointment.getMeetUrl())
                        .build();
            } else if (appointment instanceof OfflineAppointment offlineAppointment) {
                appointmentDetails = AppointmentDetailsDTO.builder()
                        .address(offlineAppointment.getAddress())
                        .build();
            }
        }

        CounselorProfileDTO counselorDTO = request.getCounselor() != null
                ?
                CounselorProfileMapper.toCounselorProfileDTO(request.getCounselor())
                : null;

        StudentProfileDTO studentDTO = request.getStudent() != null
                ?
                StudentProfileMapper.toStudentProfileDTO(request.getStudent())
                : null;

        return CounselingAppointmentRequestDTO.builder()
                .id(request.getId())
                .requireDate(request.getRequireDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(request.getStatus().name())
                .meetingType(request.getMeetingType())
                .reason(request.getReason())
                .counselor(counselorDTO)
                .student(studentDTO)
                .appointmentDetails(appointmentDetails)
                .build();
    }

    @Transactional
    public void updateAppointmentDetails(Long appointmentId, UpdateAppointmentRequestDTO updateRequest, Long counselorId) {
        // Lấy CounselingAppointmentRequest dựa trên requestId
        CounselingAppointment appointment = counselingAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found with id: " + appointmentId));

        if (!appointment.getAppointmentRequest().getCounselor().getId().equals(counselorId)) {
            throw new ForbiddenException("You do not have permission to modify this request.");
        }

        if (appointment instanceof OnlineAppointment onlineAppointment) {
            // Cập nhật meetUrl cho OnlineAppointment
            if (updateRequest.getMeetUrl() != null) {
                onlineAppointment.setMeetUrl(updateRequest.getMeetUrl());
            }
        } else if (appointment instanceof OfflineAppointment offlineAppointment) {
            // Cập nhật address cho OfflineAppointment
            if (updateRequest.getAddress() != null) {
                offlineAppointment.setAddress(updateRequest.getAddress());
            }
        } else {
            throw new BadRequestException("Invalid appointment request");
        }

        // Lưu thông tin đã cập nhật
        counselingAppointmentRepository.save(appointment);

        Student student = appointment.getAppointmentRequest().getStudent();
        Counselor counselor = appointment.getAppointmentRequest().getCounselor();

        // Use the utility method to format start and end times
        String formattedStart = DateTimeUtil.formatDateTime(appointment.getStartDateTime());
        String formattedEnd = DateTimeUtil.formatDateTime(appointment.getEndDateTime());

        notificationService.sendNotification(NotificationDTO.builder()
                .receiverId(student.getId())
                .message(String.format("Counseling appointment for student -%s- (%s) has been updated.\nAppointment Time: %s to %s",
                        student.getFullName(), student.getStudentCode(), formattedStart, formattedEnd))
                .title("Appointment Updated")
                .sender("Counselor: " + counselor.getFullName())  // or use the counselor's name if more appropriate
                .readStatus(false)
                .build());
    }
}

package com.capstone2024.scss.domain.counseling_booking.services.impl;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.booking_counseling.dto.SlotDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselingAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselorAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.StudentAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.enums.SlotStatus;
import com.capstone2024.scss.application.booking_counseling.dto.request.AppointmentRequestFilterDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.notification.dtos.NotificationDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.CounselingAppointmentRequestStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import com.capstone2024.scss.domain.counseling_booking.entities.counselor.Counselor;
import com.capstone2024.scss.domain.counseling_booking.entities.student.Student;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentRequestService;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.RabbitMQConfig;
import com.capstone2024.scss.infrastructure.repositories.CounselingAppointmentRequestRepository;
import com.capstone2024.scss.infrastructure.repositories.CounselingSlotRepository;
import com.capstone2024.scss.infrastructure.repositories.CounselorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    private final RabbitTemplate rabbitTemplate;

    public CounselingAppointmentRequestServiceImpl(CounselingAppointmentRequestRepository requestRepository, CounselingSlotRepository slotRepository, CounselorRepository counselorRepository, RabbitTemplate rabbitTemplate) {
        this.requestRepository = requestRepository;
        this.slotRepository = slotRepository;
        this.counselorRepository = counselorRepository;
        this.rabbitTemplate = rabbitTemplate;
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

        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE, NotificationDTO.builder()
                .receiverId(counselorId)
                .message("Student named -" + student.getFullName() + "-" + student.getStudentCode() + "- has sent you a counseling appointment request")
                .title("Counseling appointment request from student")
                .sender("SYSTEM")
                .build());

        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_MOBILE_QUEUE, NotificationDTO.builder()
                .receiverId(counselorId)
                .message("Student named -" + student.getFullName() + "-" + student.getStudentCode() + "- has sent you a counseling appointment request")
                .title("Counseling appointment request from student")
                .sender("SYSTEM")
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
                        filterRequest.getMeetingType() != null ? filterRequest.getMeetingType().name() : null,
                        pageable
                );
            }
            case Role.COUNSELOR -> {
                Counselor counselor = (Counselor) principle.getProfile();
                yield requestRepository.findByCounselorIdAndFilters(
                        counselor.getId(),
                        filterRequest.getDateFrom(),
                        filterRequest.getDateTo(),
                        filterRequest.getMeetingType() != null ? filterRequest.getMeetingType().name() : null,
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
//        AppointmentDetailsDTO appointmentDetails = switch (request) {
//            case OnlineAppointment online -> AppointmentDetailsDTO.builder()
//                    .meetUrl(online.getMeetUrl())
//                    .build();
//            case OfflineAppointment offline -> AppointmentDetailsDTO.builder()
//                    .address(offline.getAddress())
//                    .build();
//            default -> null;
//        };

        CounselorAppointmentDTO counselorDTO = request.getCounselor() != null
                ? CounselorAppointmentDTO.builder()
                .fullName(request.getCounselor().getFullName())
                .avatarLink(request.getCounselor().getAvatarLink())
                .build()
                : null;

        StudentAppointmentDTO studentDTO = request.getStudent() != null
                ? StudentAppointmentDTO.builder()
                .fullName(request.getStudent().getFullName())
                .avatarLink(request.getStudent().getAvatarLink())
                .studentCode(request.getStudent().getStudentCode())
                .build()
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
//                .appointmentDetails(appointmentDetails)
                .build();
    }
}

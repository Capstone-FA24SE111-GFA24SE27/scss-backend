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
import com.capstone2024.scss.application.booking_counseling.dto.enums.SlotStatus;
import com.capstone2024.scss.application.booking_counseling.dto.request.AppointmentRequestFilterDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.UpdateAppointmentRequestDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.common.helpers.NotificationHelper;
import com.capstone2024.scss.domain.common.mapper.account.CounselorProfileMapper;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.CounselingRequestMapper;
import com.capstone2024.scss.domain.common.mapper.student.StudentMapper;
import com.capstone2024.scss.domain.common.utils.DateTimeUtil;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OfflineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OnlineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.CounselingAppointmentRequestStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import com.capstone2024.scss.domain.counselor.entities.AcademicCounselor;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.counselor.entities.NonAcademicCounselor;
import com.capstone2024.scss.domain.counselor.entities.SlotOfCounselor;
import com.capstone2024.scss.domain.notification.services.NotificationService;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentRequestService;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.RabbitMQConfig;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto.RealTimeAppointmentRequestDTO;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto.RealTimeCounselingSlotDTO;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingAppointmentRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingAppointmentRequestRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingSlotRepository;
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

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CounselingAppointmentRequestServiceImpl implements CounselingAppointmentRequestService {

    private static final Logger logger = LoggerFactory.getLogger(CounselingAppointmentRequestService.class);
    private final CounselingAppointmentRequestRepository requestRepository;
    private final CounselingSlotRepository slotRepository;
    private final CounselorRepository counselorRepository;
    private final CounselingAppointmentRepository counselingAppointmentRepository;
    private final RabbitTemplate rabbitTemplate;
    private final NotificationService notificationService;
    private final StudentRepository studentRepository;

    private List<CounselingSlot> getSlotForDay(List<SlotOfCounselor> slotOfCounselors, DayOfWeek dayOfWeek) {
        return slotOfCounselors == null ?
                new ArrayList<>()
                :
                slotOfCounselors.stream()
                        .filter(slotOfCounselor -> slotOfCounselor.getDayOfWeek().equals(dayOfWeek))
                        .map(SlotOfCounselor::getCounselingSlot)
                        .collect(Collectors.toList());
    }

    @Override
    public Map<LocalDate, List<SlotDTO>> getDailySlots(Long counselorId, LocalDate from, LocalDate to, Long studentId) {
        logger.info("Fetching daily slots for counselorId: {}, from: {}, to: {}", counselorId, from, to);
        // Find the counselor
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found"));
        List<CounselingAppointmentRequest> requests = requestRepository.findByCounselorIdAndRequireDateBetween(counselorId, from, to);
        List<CounselingAppointment> appointments = counselingAppointmentRepository.findAllByCounselorIdAndDateRange(counselorId, from.atStartOfDay(), to.plusDays(1).atStartOfDay());

        List<SlotOfCounselor> slotOfCounselors = counselor.getSlotOfCounselors();

//        List<CounselingSlot> slots = counselor.getCounselingSlots();
        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime();

        Map<LocalDate, List<SlotDTO>> dailySlots = new LinkedHashMap<>();
        LocalDate currentDate = from;

        while (!currentDate.isAfter(to)) {
            if(currentDate.isBefore(counselor.getAvailableDateRange().getStartDate()) || currentDate.isAfter(counselor.getAvailableDateRange().getEndDate())) {
                dailySlots.put(currentDate, Collections.emptyList());
            } else {
                final LocalDate dateToCheck = currentDate;
                List<CounselingSlot> slots = getSlotForDay(slotOfCounselors, dateToCheck.getDayOfWeek());
                List<SlotDTO> slotDTOs = slots.stream()
                        .map(slot -> {
                            boolean isSlotTaken = requests.stream()
                                    .anyMatch(r -> r.getRequireDate().equals(dateToCheck) &&
                                            (r.getStatus() == CounselingAppointmentRequestStatus.WAITING) &&
                                            (r.getStartTime().isBefore(slot.getEndTime()) &&
                                                    r.getEndTime().isAfter(slot.getStartTime()))) ||
                                    appointments.stream()
                                            .anyMatch(a -> a.getStartDateTime().toLocalDate().equals(dateToCheck) &&
                                                    a.getStatus() != CounselingAppointmentStatus.CANCELED &&
                                                    (a.getStartDateTime().toLocalTime().isBefore(slot.getEndTime()) &&
                                                            a.getEndDateTime().toLocalTime().isAfter(slot.getStartTime())));

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
        SlotStatus slotStatus = getSlotStatus(slot, date, counselorId);
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

        rabbitTemplate.convertAndSend(RabbitMQConfig.REAL_TIME_COUNSELING_APPOINTMENT_REQUEST, RealTimeAppointmentRequestDTO.builder()
                .studentId(appointmentRequest.getStudent().getId())
                .counselorId(appointmentRequest.getCounselor().getId())
                .type(RealTimeAppointmentRequestDTO.Type.STUDENT_CREATE_NEW_REQUEST)
                .build());

//        notificationService.sendNotification(NotificationDTO.builder()
//                .receiverId(counselorId)
//                .message("Student named -" + student.getFullName() + "-" + student.getStudentCode() + "- has sent you a counseling appointment request")
//                .title("Counseling appointment request from student")
//                .sender(NotificationHelper.getSenderAsStudent(student))
//                .readStatus(false)
//                .build());

        notificationService.sendNotification(NotificationHelper.getNotificationFromStudentToCounselor(
                "Student named -" + student.getFullName() + "-" + student.getStudentCode() + "- has sent you a counseling appointment request",
                "Counseling appointment request from student",
                false,
                student,
                counselor
        ));

        // Save the appointment request
        return requestRepository.save(appointmentRequest);
    }

    private SlotStatus getSlotStatus(CounselingSlot slot, LocalDate date, Long counselorId) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found"));

        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return SlotStatus.UNAVAILABLE;
        } else if(date.isBefore(counselor.getAvailableDateRange().getStartDate()) || date.isAfter(counselor.getAvailableDateRange().getEndDate())) {
            return SlotStatus.UNAVAILABLE;
        }

        List<CounselingAppointmentRequest> requests = requestRepository.findByCounselorIdAndRequireDateBetween(counselorId, date, date);
        List<CounselingAppointment> appointments = counselingAppointmentRepository.findAllByCounselorIdAndDateRange(counselorId, date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime();

        boolean isSlotTaken = requests.stream()
                .anyMatch(r -> r.getRequireDate().equals(date) &&
                        (r.getStatus() == CounselingAppointmentRequestStatus.WAITING) &&
                        (r.getStartTime().isBefore(slot.getEndTime()) &&
                                r.getEndTime().isAfter(slot.getStartTime()))) ||
                appointments.stream()
                        .anyMatch(a -> a.getStartDateTime().toLocalDate().equals(date) &&
                                a.getStatus() != CounselingAppointmentStatus.CANCELED &&
                                (a.getStartDateTime().toLocalTime().isBefore(slot.getEndTime()) &&
                                        a.getEndDateTime().toLocalTime().isAfter(slot.getStartTime())));

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
                        filterRequest.getStatus(),
                        pageable
                );
            }
            case Role.NON_ACADEMIC_COUNSELOR, Role.ACADEMIC_COUNSELOR -> {
                Counselor counselor = (Counselor) principle.getProfile();
                yield requestRepository.findByCounselorIdAndFilters(
                        counselor.getId(),
                        filterRequest.getDateFrom(),
                        filterRequest.getDateTo(),
                        filterRequest.getMeetingType() != null ? filterRequest.getMeetingType() : null,
                        filterRequest.getStatus(),
                        pageable
                );
            }
            default -> throw new BadRequestException("Invalid role specified");
        };

        List<CounselingAppointmentRequestDTO> appointmentDTOs = page.getContent().stream()
                .map(CounselingRequestMapper::convertToDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<CounselingAppointmentRequestDTO>>builder()
                .data(appointmentDTOs)
                .totalPages(page.getTotalPages())
                .totalElements((int) page.getTotalElements())
                .build();
    }

//    private CounselingAppointmentRequestDTO convertToDTO(CounselingAppointmentRequest request) {
//        List<CounselingAppointment> appointments = request.getCounselingAppointments();
//        AppointmentDetailsDTO appointmentDetails = null;
//        if(appointments != null && !appointments.isEmpty()) {
//            CounselingAppointment appointment = appointments.getLast();
//
//            if (appointment instanceof OnlineAppointment onlineAppointment) {
//                appointmentDetails = AppointmentDetailsDTO.builder()
//                        .meetUrl(onlineAppointment.getMeetUrl())
//                        .build();
//            } else if (appointment instanceof OfflineAppointment offlineAppointment) {
//                appointmentDetails = AppointmentDetailsDTO.builder()
//                        .address(offlineAppointment.getAddress())
//                        .build();
//            }
//        }
//
//        CounselorProfileDTO counselorDTO = (request.getCounselor() != null) ? (
//                (request.getCounselor() instanceof NonAcademicCounselor) ?
//                    CounselorProfileMapper.toNonAcademicCounselorProfileDTO((NonAcademicCounselor) request.getCounselor()) : CounselorProfileMapper.toAcademicCounselorProfileDTO((AcademicCounselor) request.getCounselor())
//        ) : null;
//
//        StudentProfileDTO studentDTO = request.getStudent() != null
//                ?
//                StudentMapper.toStudentProfileDTO(request.getStudent())
//                : null;
//
//        return CounselingAppointmentRequestDTO.builder()
//                .id(request.getId())
//                .requireDate(request.getRequireDate())
//                .startTime(request.getStartTime())
//                .endTime(request.getEndTime())
//                .status(request.getStatus().name())
//                .meetingType(request.getMeetingType())
//                .reason(request.getReason())
//                .counselor(counselorDTO)
//                .student(studentDTO)
//                .appointmentDetails(appointmentDetails)
//                .build();
//    }

    @Transactional
    public void updateAppointmentDetails(Long appointmentId, UpdateAppointmentRequestDTO updateRequest, Long counselorId) {
        // Lấy CounselingAppointmentRequest dựa trên requestId
        CounselingAppointment appointment = counselingAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found with id: " + appointmentId));

        if (!appointment.getCounselor().getId().equals(counselorId)) {
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

        Student student = appointment.getStudent();
        Counselor counselor = appointment.getCounselor();

        // Use the utility method to format start and end times
        String formattedStart = DateTimeUtil.formatDateTime(appointment.getStartDateTime());
        String formattedEnd = DateTimeUtil.formatDateTime(appointment.getEndDateTime());

//        notificationService.sendNotification(NotificationDTO.builder()
//                .receiverId(student.getId())
//                .message(String.format("Counseling appointment for student -%s- (%s) has been updated.\nAppointment Time: %s to %s",
//                        student.getFullName(), student.getStudentCode(), formattedStart, formattedEnd))
//                .title("Appointment Updated")
//                .sender("Counselor: " + counselor.getFullName())  // or use the counselor's name if more appropriate
//                .readStatus(false)
//                .build());

        notificationService.sendNotification(NotificationHelper.getNotificationFromCounselorToStudent(
                String.format("Counseling appointment for student -%s- (%s) has been updated.\nAppointment Time: %s to %s",
                        student.getFullName(), student.getStudentCode(), formattedStart, formattedEnd),
                "Appointment Updated",
                false,
                counselor,
                student
        ));
    }

    @Override
    public List<CounselingAppointmentRequestDTO> findAll(LocalDate from, LocalDate to) {
        List<CounselingAppointmentRequest> rs = requestRepository.findAllByRequireDateBetween(from, to);

        return rs.stream()
                .map(CounselingRequestMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long countOpenRequest(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));
        return requestRepository.countByStatusAndStudentId(CounselingAppointmentRequestStatus.WAITING, studentId);
    }
}

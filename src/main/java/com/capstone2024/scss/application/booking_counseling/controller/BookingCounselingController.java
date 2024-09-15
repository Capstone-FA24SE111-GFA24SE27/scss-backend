package com.capstone2024.scss.application.booking_counseling.controller;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.advice.exeptions.ForbiddenException;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentRequestResponseDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselingAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.AppointmentRequestFilterDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.counceling_appointment.OfflineAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.counceling_appointment.OnlineAppointmentRequestDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.booking_counseling.dto.request.CreateCounselingAppointmentRequestDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import com.capstone2024.scss.domain.counseling_booking.entities.student.Student;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentRequestService;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/booking-counseling")
@Tag(name = "Booking Counseling", description = "API endpoints for managing booking counseling appointments and requests.")
@RequiredArgsConstructor
public class BookingCounselingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingCounselingController.class);
    private final CounselingAppointmentRequestService counselingAppointmentRequestService;
    private final CounselingAppointmentService appointmentService;

    @PostMapping("/{counselorId}/appointment-request/create")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Create a new counseling appointment request",
            description = "Allows a student to create a new appointment request with a counselor."
    )
    public ResponseEntity<Object> createAppointmentRequest(
            @Valid @RequestBody CreateCounselingAppointmentRequestDTO requestDTO,
            @PathVariable("counselorId") Long counselorId,
            BindingResult errors,
            @AuthenticationPrincipal @NotNull Account principal) {

        logger.info("Received createAppointmentRequest - Counselor ID: {}, Student: {}", counselorId, principal.getUsername());

        if (errors.hasErrors()) {
            logger.warn("Validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid appointment request", errors, HttpStatus.BAD_REQUEST);
        }

        if (!(principal.getProfile() instanceof Student)) {
            logger.warn("Attempted appointment request by non-student: {}", principal.getUsername());
            throw new BadRequestException("You are not authorized to create an appointment.");
        }

        try {
            logger.info("Processing appointment request for slotCode: {}, date: {}, counselorId: {}",
                    requestDTO.getSlotCode(), requestDTO.getDate(), counselorId);

            CounselingAppointmentRequest appointmentRequest = counselingAppointmentRequestService.createAppointmentRequest(
                    requestDTO.getSlotCode(),
                    requestDTO.getDate(),
                    counselorId,
                    requestDTO.getIsOnline(),
                    requestDTO.getReason(),
                    (Student) principal.getProfile()
            );

            CounselingAppointmentRequestResponseDTO responseDTO = CounselingAppointmentRequestResponseDTO.builder()
                    .id(appointmentRequest.getId())
                    .createdDate(appointmentRequest.getCreatedDate())
                    .requireDate(appointmentRequest.getRequireDate())
                    .startTime(appointmentRequest.getStartTime())
                    .endTime(appointmentRequest.getEndTime())
                    .status(appointmentRequest.getStatus().name())
                    .meetingType(appointmentRequest.getMeetingType().name())
                    .reason(appointmentRequest.getReason())
                    .counselorId(appointmentRequest.getCounselor().getId())
                    .build();

            logger.info("Successfully created appointment request with ID: {}", appointmentRequest.getId());

            return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);

        } catch (RuntimeException e) {
            logger.error("Error creating appointment request: {}", e.getMessage());
            throw new BadRequestException("An error occurred while processing the request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/appointment-request")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Get appointment requests",
            description = "Retrieve appointment requests filtered by date range, meeting type, and sorted by various fields."
    )
    public ResponseEntity<Object> getAppointmentsRequest(
            @AuthenticationPrincipal Account principal,
            @RequestParam(name = "dateFrom", required = false) LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) LocalDate dateTo,
            @RequestParam(name = "meetingType", required = false) MeetingType meetingType,
            @RequestParam(name = "sortBy", defaultValue = "requireDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        logger.info("Received getAppointmentsRequest - User: {}, Date From: {}, Date To: {}",
                principal.getUsername(), dateFrom, dateTo);

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new BadRequestException("Page number must be greater than 0", HttpStatus.BAD_REQUEST);
        }

        AppointmentRequestFilterDTO filterDTO = AppointmentRequestFilterDTO.builder()
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .meetingType(meetingType)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, 10))
                .build();

        PaginationDTO<List<CounselingAppointmentRequestDTO>> responseDTO = counselingAppointmentRequestService.getAppointmentsRequest(principal, filterDTO);

        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/approve/online/{requestId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Approve an online appointment request",
            description = "Allows counselors to approve an online appointment request."
    )
    public ResponseEntity<Object> approveOnlineAppointment(
            @PathVariable Long requestId,
            @AuthenticationPrincipal @NotNull Account principal,
            @RequestBody OnlineAppointmentRequestDTO dto) {
        logger.info("Approving online appointment request: {}, Counselor: {}", requestId, principal.getUsername());

        appointmentService.approveOnlineAppointment(requestId, principal.getProfile().getId(), dto);
        logger.info("Online appointment request approved successfully: {}", requestId);

        return ResponseUtil.getResponse("Online appointment approved successfully", HttpStatus.OK);
    }

    @PutMapping("/approve/offline/{requestId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Approve an offline appointment request",
            description = "Allows counselors to approve an offline appointment request."
    )
    public ResponseEntity<Object> approveOfflineAppointment(
            @PathVariable Long requestId,
            @AuthenticationPrincipal @NotNull Account principal,
            @RequestBody OfflineAppointmentRequestDTO dto) {
        logger.info("Approving offline appointment request: {}, Counselor: {}", requestId, principal.getUsername());

        appointmentService.approveOfflineAppointment(requestId, principal.getProfile().getId(), dto);
        logger.info("Offline appointment request approved successfully: {}", requestId);

        return ResponseUtil.getResponse("Offline appointment approved successfully", HttpStatus.OK);
    }

    @PutMapping("/deny/{requestId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Deny an appointment request",
            description = "Allows counselors to deny an appointment request."
    )
    public ResponseEntity<Object> denyAppointmentRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal @NotNull Account principal) {
        logger.info("Denying appointment request: {}, Counselor: {}", requestId, principal.getUsername());

        appointmentService.denyAppointmentRequest(requestId, principal.getProfile().getId());
        logger.info("Appointment request denied successfully: {}", requestId);

        return ResponseUtil.getResponse("Appointment request denied successfully", HttpStatus.OK);
    }

    @GetMapping("/appointment")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Get appointments by date range",
            description = "Retrieve counseling appointments for either a counselor or student within a date range."
    )
    public ResponseEntity<Object> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @AuthenticationPrincipal @NotNull Account principal) {

        logger.info("Received request to get appointments from {} to {}, User: {}", fromDate, toDate, principal.getUsername());

        if (fromDate.isAfter(toDate)) {
            logger.error("Invalid date range: fromDate {} is after toDate {}", fromDate, toDate);
            throw new BadRequestException("Invalid date range. 'fromDate' cannot be after 'toDate'.");
        }

        List<CounselingAppointmentDTO> appointments;
        if (principal.getRole() == Role.COUNSELOR) {
            appointments = appointmentService.getAppointmentsForCounselor(fromDate, toDate, principal.getProfile().getId());
            logger.info("Returning appointments for counselor: {}", principal.getUsername());
        } else if (principal.getRole() == Role.STUDENT) {
            appointments = appointmentService.getAppointmentsForStudent(fromDate, toDate, principal.getProfile().getId());
            logger.info("Returning appointments for student: {}", principal.getUsername());
        } else {
            logger.warn("Unauthorized attempt to access appointments by user: {}", principal.getUsername());
            throw new ForbiddenException("You do not have permission to access these appointments.");
        }

        return ResponseUtil.getResponse(appointments, HttpStatus.OK);
    }
}
package com.capstone2024.scss.application.booking_counseling.controller;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentRequestResponseDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselingAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.AppointmentFilterDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.booking_counseling.dto.request.CreateCounselingAppointmentRequestDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import com.capstone2024.scss.domain.counseling_booking.entities.student.Student;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/booking-counseling")
@Tag(name = "booking counseling", description = "API for booking counseling.")
@RequiredArgsConstructor
public class BookingCounselingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingCounselingController.class);
    private final CounselingAppointmentRequestService counselingAppointmentRequestService;

    @PostMapping("/{counselorId}/appointment-request/create")
    public ResponseEntity<Object> createAppointmentRequest(
            @Valid @RequestBody CreateCounselingAppointmentRequestDTO requestDTO,
            @PathVariable("counselorId") Long counselorId,
            BindingResult errors,
            @AuthenticationPrincipal @NotNull Account principal) {

        if (errors.hasErrors()) {
            logger.warn("Login request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid login request", errors, HttpStatus.BAD_REQUEST);
        }

        logger.info("Received request to create appointment for slotCode: {}, date: {}, counselorId: {}, isOnline: {}, reason: {}",
                requestDTO.getSlotCode(), requestDTO.getDate(), counselorId, requestDTO.getIsOnline(), requestDTO.getReason());

        if(!(principal.getProfile() instanceof Student)) {
            throw new BadRequestException("You are not student");
        }

        try {
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
                    .softDelete(appointmentRequest.isSoftDelete())
                    .requireDate(appointmentRequest.getRequireDate())
                    .startTime(appointmentRequest.getStartTime())
                    .endTime(appointmentRequest.getEndTime())
                    .status(appointmentRequest.getStatus().name())
                    .meetingType(appointmentRequest.getMeetingType().name())
                    .reason(appointmentRequest.getReason())
                    .counselorId(appointmentRequest.getCounselor().getId())
                    .build();

            return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error creating appointment request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/appointment-request")
    public ResponseEntity<Object> getAppointmentsRequest(
            @AuthenticationPrincipal Account principal,
            @RequestParam(name = "dateFrom", required = false) LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) LocalDate dateTo,
            @RequestParam(name = "meetingType", required = false) MeetingType meetingType,
            @RequestParam(name = "sortBy", defaultValue = "requireDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new BadRequestException("Page must be positive (page > 0)", HttpStatus.BAD_REQUEST);
        }

        AppointmentFilterDTO filterDTO = AppointmentFilterDTO.builder()
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .meetingType(meetingType)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, 10))
                .build();

        PaginationDTO<List<CounselingAppointmentRequestDTO>> responseDTO = counselingAppointmentRequestService.getAppointmentsRequest(principal, filterDTO);

        return  ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }
}
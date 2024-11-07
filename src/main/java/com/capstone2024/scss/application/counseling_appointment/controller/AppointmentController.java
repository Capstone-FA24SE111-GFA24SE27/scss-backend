package com.capstone2024.scss.application.counseling_appointment.controller;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselingAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.CreateCounselingAppointmentRequestDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.counseling_appointment.dto.AppointmentReportResponse;
import com.capstone2024.scss.application.counseling_appointment.dto.request.CreateCounselingAppointmentDTO;
import com.capstone2024.scss.application.counseling_appointment.dto.request.appoinment_report.AppointmentReportRequest;
import com.capstone2024.scss.application.counseling_appointment.dto.request.counseling_appointment.AppointmentFilterDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.CounselingRequestMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentService;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.student.entities.Student;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointments")
@Tag(name = "Appointment", description = "API endpoints for managing appointments")
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final CounselingAppointmentService appointmentService;

    @PostMapping("/create/{studentId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Create a new counseling appointment",
            description = "Allows a counselor to create a new appointment with a student."
    )
    public ResponseEntity<Object> createAppointment(
            @Valid @RequestBody CreateCounselingAppointmentDTO requestDTO,
            @PathVariable Long studentId,
            BindingResult errors,
            @AuthenticationPrincipal @NotNull Account principal) {

        Long counselorId = principal.getProfile().getId();

        logger.info("Received createAppointment - Counselor ID: {}", principal.getUsername());

        if (errors.hasErrors()) {
            logger.warn("Validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid appointment", errors, HttpStatus.BAD_REQUEST);
        }

        if (!(principal.getProfile() instanceof Counselor)) {
            logger.warn("Attempted appointment request by student: {}", principal.getUsername());
            throw new BadRequestException("You are not authorized to create an appointment.");
        }
        logger.info("Processing appointment for slotCode: {}, date: {}, counselorId: {}",
                requestDTO.getSlotCode(), requestDTO.getDate(), counselorId);

        CounselingAppointmentDTO appointment = appointmentService.createAppointment(requestDTO, counselorId, studentId);

        logger.info("Successfully created appointment with ID: {}", appointment.getId());

        return ResponseUtil.getResponse(appointment, HttpStatus.OK);
    }

    @PostMapping("/demand/{demandId}/create")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Create a new counseling appointment for demand",
            description = "Allows a counselor to create a new appointment for demand with a student."
    )
    public ResponseEntity<Object> createAppointmentForDemand(
            @Valid @RequestBody CreateCounselingAppointmentDTO requestDTO,
            @PathVariable Long demandId,
            BindingResult errors,
            @AuthenticationPrincipal @NotNull Account principal) {

        Long counselorId = principal.getProfile().getId();

        logger.info("Received createAppointment - Counselor ID: {}", principal.getUsername());

        if (errors.hasErrors()) {
            logger.warn("Validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid appointment", errors, HttpStatus.BAD_REQUEST);
        }

        if (!(principal.getProfile() instanceof Counselor)) {
            logger.warn("Attempted appointment request by student: {}", principal.getUsername());
            throw new BadRequestException("You are not authorized to create an appointment.");
        }
        logger.info("Processing appointment for slotCode: {}, date: {}, counselorId: {}",
                requestDTO.getSlotCode(), requestDTO.getDate(), counselorId);

        CounselingAppointmentDTO appointment = appointmentService.createAppointmentForDemand(requestDTO, counselorId, null, demandId);

        logger.info("Successfully created appointment with ID: {}", appointment.getId());

        return ResponseUtil.getResponse(appointment, HttpStatus.OK);
    }

    @GetMapping("/counselor")
    @Operation(
            summary = "Retrieve appointments for counselor with optional filters",
            description = "Fetches a list of appointments for counselors with optional filters for studentCode, date range, status, sorting, and pagination.",
            parameters = {
                    @Parameter(name = "studentCode", description = "Filter by student code"),
                    @Parameter(name = "fromDate", description = "Start date for the date range filter (yyyy-MM-dd)"),
                    @Parameter(name = "toDate", description = "End date for the date range filter (yyyy-MM-dd)"),
                    @Parameter(name = "status", description = "Filter by appointment status"),
                    @Parameter(name = "sortBy", description = "Field to sort by"),
                    @Parameter(name = "SortDirection", description = "Sort direction (ASC or DESC)"),
                    @Parameter(name = "page", description = "Page number for pagination")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched appointments with applied filters."),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters.")
            }
    )
    public ResponseEntity<Object> getAppointmentsForCounselor(
            @RequestParam(name = "studentCode", required = false) String studentCode,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(name = "status", required = false) CounselingAppointmentStatus status,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "SortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @NotNull @AuthenticationPrincipal Account principle) {

        AppointmentFilterDTO filterDTO = AppointmentFilterDTO.builder()
                .studentCode(studentCode)
                .fromDate(fromDate)
                .toDate(toDate)
                .status(status)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .build();

        PaginationDTO<List<CounselingAppointmentDTO>> responseDTO = appointmentService.getAppointmentsWithFilterForCounselor(filterDTO, (Counselor) principle.getProfile());

        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/student")
    @Operation(
            summary = "Retrieve appointments for student with optional filters",
            description = "Fetches a list of appointments for students with optional filters for date range, status, sorting, and pagination.",
            parameters = {
                    @Parameter(name = "fromDate", description = "Start date for the date range filter (yyyy-MM-dd)", example = "2024-01-01"),
                    @Parameter(name = "toDate", description = "End date for the date range filter (yyyy-MM-dd)", example = "2024-01-31"),
                    @Parameter(name = "status", description = "Filter by appointment status"),
                    @Parameter(name = "sortBy", description = "Field to sort by"),
                    @Parameter(name = "SortDirection", description = "Sort direction (ASC or DESC)"),
                    @Parameter(name = "page", description = "Page number for pagination")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched appointments with applied filters."),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters.")
            }
    )
    public ResponseEntity<Object> getAppointmentsForStudent(
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(name = "status", required = false) CounselingAppointmentStatus status,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "SortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @NotNull @AuthenticationPrincipal Account principle) {

        AppointmentFilterDTO filterDTO = AppointmentFilterDTO.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .status(status)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .build();

        PaginationDTO<List<CounselingAppointmentDTO>> responseDTO = appointmentService.getAppointmentsWithFilterForStudent(filterDTO, (Student) principle.getProfile());

        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<Object> getOneAppointments(
            @PathVariable Long appointmentId
    ) {
        CounselingAppointmentDTO responseDTO = appointmentService.getOneAppointment(appointmentId);

        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/report/{appointmentId}")
    public ResponseEntity<Object> createAppointmentReport(
            @Valid @RequestBody AppointmentReportRequest request,
            BindingResult errors,
            @NotNull @AuthenticationPrincipal Account principle,
            @PathVariable Long appointmentId) {
        if (errors.hasErrors()) {
            logger.warn("Validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid appointment request", errors, HttpStatus.BAD_REQUEST);
        }

        AppointmentReportResponse response = appointmentService.createAppointmentReport(request, appointmentId, (Counselor) principle.getProfile());
        return ResponseUtil.getResponse(response, HttpStatus.OK);
    }

    @GetMapping("/report/{appointmentId}")
    public ResponseEntity<Object> getReportByAppointmentId(@PathVariable Long appointmentId,
                                                                              @NotNull @AuthenticationPrincipal Account principle) {
        AppointmentReportResponse response = appointmentService.getAppointmentReportByAppointmentId(appointmentId, (Counselor) principle.getProfile());
        return ResponseUtil.getResponse(response, HttpStatus.OK);
    }
}

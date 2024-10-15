package com.capstone2024.scss.application.counselor.controller;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.advice.exeptions.ForbiddenException;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselingAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.AppointmentRequestFilterDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.counseling_appointment.dto.AppointmentReportResponse;
import com.capstone2024.scss.application.counseling_appointment.dto.request.counseling_appointment.AppointmentFilterDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentRequestService;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.counselor.services.CounselorService;
import com.capstone2024.scss.domain.counselor.services.ManageCounselorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/manage/counselors")
@Tag(name = "manage counselors", description = "API for manage counselors.")
@RequiredArgsConstructor
public class ManageCounselorController {

    private static final Logger logger = LoggerFactory.getLogger(CounselorController.class);
    private final ManageCounselorService manageCounselorService;

    @GetMapping("/appointment-request/{counselorId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Get appointment requests",
            description = "Retrieve appointment requests filtered by date range, meeting type, and sorted by various fields."
    )
    public ResponseEntity<Object> getAppointmentsRequest(
            @PathVariable("counselorId") Long counselorId,
            @RequestParam(name = "dateFrom", required = false) LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) LocalDate dateTo,
            @RequestParam(name = "meetingType", required = false) MeetingType meetingType,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

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

        PaginationDTO<List<CounselingAppointmentRequestDTO>> responseDTO = manageCounselorService.getAppointmentsRequest(counselorId, filterDTO);

        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/appointment/{counselorId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Get appointments by date range",
            description = "Retrieve counseling appointments for either a counselor or student within a date range."
    )
    public ResponseEntity<Object> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PathVariable("counselorId") Long counselorId) {

        if (fromDate.isAfter(toDate)) {
            logger.error("Invalid date range: fromDate {} is after toDate {}", fromDate, toDate);
            throw new BadRequestException("Invalid date range. 'fromDate' cannot be after 'toDate'.");
        }

        List<CounselingAppointmentDTO> appointments = manageCounselorService.getAppointmentsForCounselor(fromDate, toDate, counselorId);

        return ResponseUtil.getResponse(appointments, HttpStatus.OK);
    }

    @GetMapping("/appointment/filter/{counselorId}")
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
            @PathVariable("counselorId") Long counselorId,
            @RequestParam(name = "studentCode", required = false) String studentCode,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(name = "status", required = false) CounselingAppointmentStatus status,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "SortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        AppointmentFilterDTO filterDTO = AppointmentFilterDTO.builder()
                .studentCode(studentCode)
                .fromDate(fromDate)
                .toDate(toDate)
                .status(status)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .build();

        PaginationDTO<List<CounselingAppointmentDTO>> responseDTO = manageCounselorService.getAppointmentsWithFilterForCounselor(filterDTO, counselorId);

        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/report/{appointmentId}/{counselorId}")
    public ResponseEntity<Object> getReportByAppointmentId(
            @PathVariable("counselorId") Long counselorId,
            @PathVariable Long appointmentId,
            @NotNull @AuthenticationPrincipal Account principle) {
        AppointmentReportResponse response = manageCounselorService.getAppointmentReportByAppointmentId(appointmentId, counselorId);
        return ResponseUtil.getResponse(response, HttpStatus.OK);
    }
}

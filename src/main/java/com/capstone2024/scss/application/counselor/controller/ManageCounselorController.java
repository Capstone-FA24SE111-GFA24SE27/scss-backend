package com.capstone2024.scss.application.counselor.controller;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.booking_counseling.dto.AppointmentFeedbackDTO;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselingAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.AppointmentRequestFilterDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.counseling_appointment.dto.AppointmentReportResponse;
import com.capstone2024.scss.application.counseling_appointment.dto.request.counseling_appointment.AppointmentFilterDTO;
import com.capstone2024.scss.application.counselor.dto.AvailableDateRangeDTO;
import com.capstone2024.scss.application.counselor.dto.CounselingSlotDTO;
import com.capstone2024.scss.application.counselor.dto.ManageCounselorDTO;
import com.capstone2024.scss.application.counselor.dto.counseling_slot.CounselingSlotCreateDTO;
import com.capstone2024.scss.application.counselor.dto.counseling_slot.CounselingSlotUpdateDTO;
import com.capstone2024.scss.application.counselor.dto.request.*;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.CounselingSlotMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentService;
import com.capstone2024.scss.domain.counseling_booking.services.impl.CounselingSlotService;
import com.capstone2024.scss.domain.counselor.entities.AvailableDateRange;
import com.capstone2024.scss.domain.counselor.entities.SlotOfCounselor;
import com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
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
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/manage/counselors")
@Tag(name = "manage counselors", description = "API for manage counselors.")
@RequiredArgsConstructor
public class ManageCounselorController {

    private static final Logger logger = LoggerFactory.getLogger(CounselorController.class);
    private final ManageCounselorService manageCounselorService;
    private final CounselingSlotService counselingSlotService;
    private final CounselingAppointmentService appointmentService;

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
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {

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
                .pagination(PageRequest.of(page - 1, size))
                .build();

        PaginationDTO<List<CounselingAppointmentRequestDTO>> responseDTO = manageCounselorService.getAppointmentsRequestOfCounselorForManage(counselorId, filterDTO);

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
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {

        AppointmentFilterDTO filterDTO = AppointmentFilterDTO.builder()
                .studentCode(studentCode)
                .fromDate(fromDate)
                .toDate(toDate)
                .status(status)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .size(size)
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

    @PutMapping("/{counselorId}/status")
    public ResponseEntity<Object> updateCounselorStatus(
            @PathVariable Long counselorId,
            @RequestParam CounselorStatus status
    ) {
        manageCounselorService.updateCounselorStatus(counselorId, status);
        return ResponseUtil.getResponse("Counselor status updated successfully", HttpStatus.OK);
    }

    @PutMapping("/{counselorId}/available-date-range")
    public ResponseEntity<String> updateAvailableDateRange(
            @PathVariable Long counselorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        manageCounselorService.updateAvailableDateRange(counselorId, startDate, endDate);
        return ResponseEntity.ok("Available date range updated successfully");
    }

    @GetMapping("/available-date-range")
    public ResponseEntity<Object> getAvailableDateRange(
            @RequestParam("counselorId") Long counselorId) {
        AvailableDateRange availableDateRange = manageCounselorService.getAvailableDateRangeByCounselorId(counselorId);
        return ResponseUtil.getResponse(AvailableDateRangeDTO.builder()
                        .startDate(availableDateRange.getStartDate())
                        .endDate(availableDateRange.getEndDate())
                        .build(), HttpStatus.OK);
    }

    @GetMapping("/counselling-slots")
    public ResponseEntity<Object> getAllCounselingSlots() {
        List<CounselingSlot> slots = manageCounselorService.getAllCounselingSlots();
        return ResponseUtil.getResponse(slots.stream().map(CounselingSlotMapper::toDTO).toList(), HttpStatus.OK);
    }

    @PostMapping("/counselling-slots")
    public ResponseEntity<?> createOne(@RequestBody CounselingSlotCreateDTO createDTO) {
        CounselingSlotDTO createdSlot = counselingSlotService.createOne(createDTO);
        return ResponseUtil.getResponse(createdSlot, HttpStatus.CREATED);
    }

    @GetMapping("/counselling-slots/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        CounselingSlotDTO slot = counselingSlotService.getOne(id);
        return ResponseUtil.getResponse(slot, HttpStatus.OK);
    }

    @PutMapping("/counselling-slots/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CounselingSlotUpdateDTO updateDTO) {
        CounselingSlotDTO updatedSlot = counselingSlotService.update(id, updateDTO);
        return ResponseUtil.getResponse(updatedSlot, HttpStatus.OK);
    }

    @DeleteMapping("/counselling-slots/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        counselingSlotService.delete(id);
        return ResponseUtil.getResponse("Deleted successfully", HttpStatus.OK);
    }

//    @PostMapping("/counselling-slots")
//    public ResponseEntity<Object> createCounselingSlot(BindingResult errors, @Valid @RequestBody CreateCounselingSlotRequestDTO createCounselingSlotDTO) {
//        if (errors.hasErrors()) {
//            logger.warn("Login request failed due to validation errors: {}", errors.getAllErrors());
//            throw new BadRequestException("Invalid login request", errors, HttpStatus.BAD_REQUEST);
//        }
//        CounselingSlotDTO createdSlot = manageCounselorService.createCounselingSlot(createCounselingSlotDTO);
//        return ResponseUtil.getResponse(createdSlot, HttpStatus.OK);
//    }

    @GetMapping("/{counselorId}/counseling-slots")
    public ResponseEntity<Object> getCounselingSlotsByCounselorId(@PathVariable Long counselorId) {
        List<SlotOfCounselor> slots = manageCounselorService.getCounselingSlotsByCounselorId(counselorId);
        return ResponseUtil.getResponse(slots.stream().map(CounselingSlotMapper::toDTOSlotOfCounselor).toList(), HttpStatus.OK);
    }

    @PutMapping("/{counselorId}/assign-slot")
    public ResponseEntity<String> assignSlotToCounselor(
            @PathVariable Long counselorId,
            @RequestParam Long slotId,
            @RequestParam DayOfWeek dayOfWeek
            ) {
        manageCounselorService.assignSlotToCounselor(counselorId, slotId, dayOfWeek);
        return ResponseEntity.ok("Gán CounselingSlot thành công");
    }

    @DeleteMapping("/{counselorId}/unassign-slot")
    public ResponseEntity<String> unassignSlotFromCounselor(
            @PathVariable Long counselorId,
            @RequestParam Long slotId
    ) {
        manageCounselorService.unassignSlotFromCounselor(counselorId, slotId);
        return ResponseEntity.ok("Gỡ gán CounselingSlot thành công");
    }

    @GetMapping("/feedback/filter/{counselorId}")
    @Operation(
            summary = "Retrieve feedback for counselor with optional filters",
            description = "Fetches a list of feedback for a counselor with optional filters for keyword, date range, rating range, and pagination.",
            parameters = {
                    @Parameter(name = "keyword", description = "Filter by comment keyword"),
                    @Parameter(name = "dateFrom", description = "Start date for the date range filter (yyyy-MM-dd)"),
                    @Parameter(name = "dateTo", description = "End date for the date range filter (yyyy-MM-dd)"),
                    @Parameter(name = "ratingFrom", description = "Minimum rating"),
                    @Parameter(name = "ratingTo", description = "Maximum rating"),
                    @Parameter(name = "sortBy", description = "Field to sort by"),
                    @Parameter(name = "SortDirection", description = "Sort direction (ASC or DESC)"),
                    @Parameter(name = "page", description = "Page number for pagination")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched feedback with applied filters."),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters.")
            }
    )
    public ResponseEntity<Object> getFeedbackForCounselor(
            @PathVariable("counselorId") Long counselorId,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "dateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(name = "ratingFrom", required = false) BigDecimal ratingFrom,
            @RequestParam(name = "ratingTo", required = false) BigDecimal ratingTo,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") String sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        FeedbackFilterDTO filterDTO = FeedbackFilterDTO.builder()
                .keyword(keyword)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .ratingFrom(ratingFrom)
                .ratingTo(ratingTo)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .build();

        PaginationDTO<List<AppointmentFeedbackDTO>> responseDTO = manageCounselorService.getFeedbackWithFilterForCounselor(filterDTO, counselorId);

        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/academic")
    public ResponseEntity<Object> getAcaCounselors(
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "ratingFrom", required = false) BigDecimal ratingFrom,
            @RequestParam(name = "ratingTo", required = false) BigDecimal ratingTo,
            @RequestParam(name = "availableFrom", required = false) LocalDate availableFrom,
            @RequestParam(name = "availableTo", required = false) LocalDate availableTo,
            @RequestParam(name = "specializationId", required = false) Long specializationId,
            @RequestParam(name = "majorId", required = false) Long majorId,
            @RequestParam(name = "departmentId", required = false) Long departmentId,
            @RequestParam(name = "gender", required = false) Gender gender,
            @RequestParam(name = "status", required = false) CounselorStatus status,
            @RequestParam(name = "SortDirection", defaultValue = "ASC") SortDirection sortDirection,
            @RequestParam(name = "sortBy", defaultValue = "fullName") String sortBy,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }


        AcademicCounselorFilterRequestDTO filterRequest = AcademicCounselorFilterRequestDTO.builder()
                .search(search.isEmpty() ? null : search.trim())
                .ratingFrom(ratingFrom)
                .ratingTo(ratingTo)
                .availableFrom(availableFrom)
                .availableTo(availableTo)
                .specializationId(specializationId)
                .departmentId(departmentId)
                .majorId(majorId)
                .sortBy(sortBy)
                .gender(gender)
                .status(status)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, 10, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .build();

        PaginationDTO<List<ManageCounselorDTO>> responseDTO = manageCounselorService.getAcademicCounselorsWithFilter(filterRequest);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/non-academic")
    public ResponseEntity<Object> getNonAcaCounselors(
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "ratingFrom", required = false) BigDecimal ratingFrom,
            @RequestParam(name = "ratingTo", required = false) BigDecimal ratingTo,
            @RequestParam(name = "availableFrom", required = false) LocalDate availableFrom,
            @RequestParam(name = "availableTo", required = false) LocalDate availableTo,
            @RequestParam(name = "expertiseId", required = false) Long expertiseId, // Add expertise ID
            @RequestParam(name = "SortDirection", defaultValue = "ASC") SortDirection sortDirection,
            @RequestParam(name = "gender", required = false) Gender gender,
            @RequestParam(name = "status", required = false) CounselorStatus status,
            @RequestParam(name = "sortBy", defaultValue = "fullName") String sortBy,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        NonAcademicCounselorFilterRequestDTO filterRequest = NonAcademicCounselorFilterRequestDTO.builder()
                .search(search.isEmpty() ? null : search.trim())
                .ratingFrom(ratingFrom)
                .ratingTo(ratingTo)
                .availableFrom(availableFrom)
                .availableTo(availableTo)
                .expertiseId(expertiseId) // Set expertise ID
                .sortBy(sortBy)
                .gender(gender)
                .status(status)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, 10, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .build();

        PaginationDTO<List<ManageCounselorDTO>> responseDTO = manageCounselorService.getNonAcademicCounselorsWithFilter(filterRequest);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{counselorId}")
    @Operation(
            summary = "Retrieve a specific counselor by counselor ID",
            description = "Fetches detailed information about a specific counselor by the associated counselor ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched the counselor."),
                    @ApiResponse(responseCode = "404", description = "Counselor not found.")
            }
    )
    public ResponseEntity<Object> getOneCounselor(@PathVariable("counselorId") Long counselorId) {

        ManageCounselorDTO responseDTO = manageCounselorService.getOneCounselor(counselorId);

        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/schedule/appointment/counselor/{counselorId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Get appointments by date range",
            description = "Retrieve counseling appointments for either a counselor or student within a date range."
    )
    public ResponseEntity<Object> getAppointmentsByDateRangeForCounselor(
            @PathVariable Long counselorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @AuthenticationPrincipal @NotNull Account principal) {

        logger.info("Received request to get appointments from {} to {}, User: {}", fromDate, toDate, principal.getUsername());

        if (fromDate.isAfter(toDate)) {
            logger.error("Invalid date range: fromDate {} is after toDate {}", fromDate, toDate);
            throw new BadRequestException("Invalid date range. 'fromDate' cannot be after 'toDate'.");
        }

        List<CounselingAppointmentDTO> appointments;
        appointments = appointmentService.getAppointmentsForCounselor(fromDate, toDate, counselorId);
        logger.info("Returning appointments for counselor: {}", principal.getUsername());

        return ResponseUtil.getResponse(appointments, HttpStatus.OK);
    }

    @GetMapping("/schedule/appointment/student/{studentId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Get appointments by date range",
            description = "Retrieve counseling appointments for either a counselor or student within a date range."
    )
    public ResponseEntity<Object> getAppointmentsByDateRangeForStudent(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @AuthenticationPrincipal @NotNull Account principal) {

        logger.info("Received request to get appointments from {} to {}, User: {}", fromDate, toDate, principal.getUsername());

        if (fromDate.isAfter(toDate)) {
            logger.error("Invalid date range: fromDate {} is after toDate {}", fromDate, toDate);
            throw new BadRequestException("Invalid date range. 'fromDate' cannot be after 'toDate'.");
        }

        List<CounselingAppointmentDTO> appointments;
        appointments = appointmentService.getAppointmentsForStudent(fromDate, toDate, studentId);
        logger.info("Returning appointments for student: {}", principal.getUsername());

        return ResponseUtil.getResponse(appointments, HttpStatus.OK);
    }

}

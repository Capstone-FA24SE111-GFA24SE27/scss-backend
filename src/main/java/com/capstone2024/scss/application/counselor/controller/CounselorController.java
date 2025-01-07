package com.capstone2024.scss.application.counselor.controller;

import com.capstone2024.scss.application.account.dto.AcademicCounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.NonAcademicCounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.booking_counseling.dto.SlotDTO;
import com.capstone2024.scss.application.common.dto.SpecializationDTO;
import com.capstone2024.scss.application.counselor.dto.request.AcademicCounselorFilterRequestDTO;
import com.capstone2024.scss.application.counselor.dto.request.CounselorFilterRequestDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.counselor.dto.request.NonAcademicCounselorFilterRequestDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.application.counselor.dto.ExpertiseDTO;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentRequestService;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import com.capstone2024.scss.domain.counselor.services.CounselorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/counselors")
@Tag(name = "counselors", description = "API for retrieving counselors.")
@RequiredArgsConstructor
public class CounselorController {

    private static final Logger logger = LoggerFactory.getLogger(CounselorController.class);
    private final CounselorService counselorService;
    private final CounselingAppointmentRequestService counselingAppointmentRequestService;

    @GetMapping
    @Operation(
            summary = "Retrieve counselors with optional filters",
            description = "Fetches a list of counselors filtered by keyword and rating, with pagination and sorting.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(name = "search", description = "Search keyword for counselor's profile or name"),
                    @io.swagger.v3.oas.annotations.Parameter(name = "SortDirection", description = "Sort direction (ASC or DESC)"),
                    @io.swagger.v3.oas.annotations.Parameter(name = "sortBy", description = "Field to sort by"),
                    @io.swagger.v3.oas.annotations.Parameter(name = "page", description = "Page number for pagination")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully fetched counselors with applied filters.",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PaginationDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters.",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json"
                            )
                    )
            }
    )
    public ResponseEntity<Object> getCounselorsWithFilter(
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "ratingFrom", required = false) BigDecimal ratingFrom,
            @RequestParam(name = "ratingTo", required = false) BigDecimal ratingTo,
            @RequestParam(name = "SortDirection", defaultValue = "ASC") SortDirection sortDirection,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "page", defaultValue = "1") Integer page
    ) {
        logger.debug("Entering getCounselorsWithFilter method with parameters - Search: {}, SortDirection: {}, SortBy: {}, Page: {}", search, sortDirection, sortBy, page);

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        Sort sort = Sort.by(sortBy);
        sort = sortDirection == SortDirection.ASC ? sort.ascending() : sort.descending();

        CounselorFilterRequestDTO filterRequest = CounselorFilterRequestDTO.builder()
                .search(search != null && !search.trim().isEmpty() ? search.trim() : null)
                .ratingFrom(ratingFrom)
                .ratingTo(ratingTo)
                .soreDirection(sortDirection)
                .sortBy(sortBy)
                .pagination(PageRequest.of(page - 1, 10, sort))
                .build();

        PaginationDTO<List<CounselorProfileDTO>> responseDTO = counselorService.getCounselorsWithFilter(filterRequest);

        logger.debug("Successfully fetched counselors with filter - Total elements: {}", responseDTO.getTotalElements());

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

        CounselorProfileDTO responseDTO = counselorService.getOneCounselor(counselorId);

        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @Operation(summary = "Get daily slots", description = "Fetch daily slots for a counselor within a specified date range.")
    @GetMapping("/daily-slots/{counselorId}")
    public ResponseEntity<Object> getDailySlots(
            @PathVariable("counselorId") Long counselorId,
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to,
            @AuthenticationPrincipal @NotNull Account principal) {
        logger.info("Request received to get daily slots for counselorId: {}, from: {}, to: {}", counselorId, from, to);
        Map<LocalDate, List<SlotDTO>> slots = counselingAppointmentRequestService.getDailySlots(counselorId, from, to, principal.getProfile().getId());
        logger.info("Returning daily slots for counselorId: {}, from: {}, to: {}", counselorId, from, to);
        return ResponseUtil.getResponse(slots, HttpStatus.OK);
    }

    @GetMapping("/expertise")
    public ResponseEntity<Object> getAllExpertises() {
        List<ExpertiseDTO> expertiseList = counselorService.getAllExpertises();
        return ResponseUtil.getResponse(expertiseList, HttpStatus.OK);
    }

    @GetMapping("/random/match/reason/meaning/{studentId}")
    public ResponseEntity<Object> getReasonMeaning(@RequestParam String reason,
                                                   @PathVariable Long studentId) {
        String meaning = counselorService.getReasonMeaning(reason, studentId);
        return ResponseUtil.getResponse(meaning, HttpStatus.OK);
    }

    @GetMapping("/non-academic/random/match")
    public ResponseEntity<Object> findBestCounselorNonAcademic(
            @RequestParam(name = "slotId", required = true) Long slotId,
            @RequestParam(name = "date", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(name = "gender", required = false) Gender gender,
//            @RequestParam(name = "expertiseId", required = false) Long expertiseId,
            @RequestParam(name = "reason", required = false) String reason) {

        List<CounselorProfileDTO> counselor = counselorService.findBestAvailableCounselorForNonAcademic(slotId, date, gender, reason, null);
        return ResponseUtil.getResponse(counselor, HttpStatus.OK);
    }

    @GetMapping("/random/match")
    public ResponseEntity<Object> findBestCounselor(
            @RequestParam(name = "slotId", required = false) Long slotId,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(name = "gender", required = false) Gender gender,
            @RequestParam(name = "reason", required = true) String reason
    ) {

        List<CounselorProfileDTO> counselor = counselorService.findBestAvailableCounselor(slotId, date, gender, reason);
        return ResponseUtil.getResponse(counselor, HttpStatus.OK);
    }

    @GetMapping("/academic/random/match/{studentId}")
    public ResponseEntity<Object> findBestCounselorAcademic(
            @RequestParam(name = "slotId", required = true) Long slotId,
            @RequestParam(name = "date", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(name = "gender", required = false) Gender gender,
            @PathVariable Long studentId,
            @RequestParam(name = "reason", required = true) String reason,
//            @RequestParam(name = "specializationId", required = false) Long specializationId,
            @RequestParam(name = "departmentId", required = false) Long departmentId,
            @RequestParam(name = "majorId", required = false) Long majorId
            ) {

        List<CounselorProfileDTO> counselor = counselorService.findBestAvailableCounselorForAcademic(slotId, date, gender, studentId, reason, departmentId, majorId, null);
        return ResponseUtil.getResponse(counselor, HttpStatus.OK);
    }

    @GetMapping("/counseling-slot")
    public ResponseEntity<Object> getAllCounselingSlots(
            @RequestParam(name = "date", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @NotNull @AuthenticationPrincipal Account principle
    ) {
        List<SlotDTO> slots = counselorService.getAllCounselingSlots(date, principle.getProfile().getId());
        return ResponseUtil.getResponse(slots, HttpStatus.OK);
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
            @RequestParam(name = "sortBy", defaultValue = "fullName") String sortBy,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {

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
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, size, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .build();

        PaginationDTO<List<NonAcademicCounselorProfileDTO>> responseDTO = counselorService.getNonAcademicCounselorsWithFilter(filterRequest);
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
            @RequestParam(name = "SortDirection", defaultValue = "ASC") SortDirection sortDirection,
            @RequestParam(name = "sortBy", defaultValue = "fullName") String sortBy,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {

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
                .majorId(majorId)
                .departmentId(departmentId)
                .gender(gender)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .pagination(PageRequest.of(page - 1, size, Sort.by(sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .build();

        PaginationDTO<List<AcademicCounselorProfileDTO>> responseDTO = counselorService.getAcademicCounselorsWithFilter(filterRequest);
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/specialization")
    public ResponseEntity<Object> getAllSpecialization() {
        List<SpecializationDTO> specializations = counselorService.getAllSpecialization();
        return ResponseUtil.getResponse(specializations, HttpStatus.OK);
    }

    @GetMapping("/non-academic/{id}")
    public ResponseEntity<NonAcademicCounselorProfileDTO> getOneNonAcademicCounselor(@PathVariable Long id) {
        NonAcademicCounselorProfileDTO counselorDTO = counselorService.getNonAcademicCounselorById(id);
        return new ResponseEntity<>(counselorDTO, HttpStatus.OK);
    }

    @GetMapping("/academic/{id}")
    public ResponseEntity<AcademicCounselorProfileDTO> getOneAcademicCounselor(@PathVariable Long id) {
        AcademicCounselorProfileDTO counselorDTO = counselorService.getAcademicCounselorById(id);
        return new ResponseEntity<>(counselorDTO, HttpStatus.OK);
    }

}
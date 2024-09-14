package com.capstone2024.scss.application.counselor.controller;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.counselor.dto.CounselorDTO;
import com.capstone2024.scss.application.booking_counseling.dto.SlotDTO;
import com.capstone2024.scss.application.counselor.dto.request.CounselorFilterRequestDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentRequestService;
import com.capstone2024.scss.domain.counseling_booking.services.CounselorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
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

        CounselorFilterRequestDTO filterRequest = CounselorFilterRequestDTO.builder()
                .search(search != null && !search.trim().isEmpty() ? search.trim() : null)
                .ratingFrom(ratingFrom)
                .ratingTo(ratingTo)
                .soreDirection(sortDirection)
                .sortBy(sortBy)
                .pagination(PageRequest.of(page - 1, 10))
                .build();

        PaginationDTO<List<CounselorDTO>> responseDTO = counselorService.getCounselorsWithFilter(filterRequest);

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

        CounselorDTO responseDTO = counselorService.getOneCounselor(counselorId);

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
}
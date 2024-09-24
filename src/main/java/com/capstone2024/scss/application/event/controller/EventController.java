package com.capstone2024.scss.application.event.controller;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.event.dto.EventDTO;
import com.capstone2024.scss.application.event.dto.request.EventFilterDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Events", description = "API for managing events")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Operation(summary = "Get all events", description = "Retrieve a list of events filtered by date, semester, keyword, and category.")
    @GetMapping
    public ResponseEntity<Object> getAllEvents(
            @Parameter(description = "Start date for filtering events")
            @RequestParam(name = "dateFrom", required = false) LocalDate dateFrom,
            @Parameter(description = "End date for filtering events")
            @RequestParam(name = "dateTo", required = false) LocalDate dateTo,
            @Parameter(description = "ID of the semester to filter events")
            @RequestParam(name = "semesterId", required = false) Long semesterId,
            @Parameter(description = "Keyword to search events")
            @RequestParam(name = "keyword", required = false) String keyword,
            @Parameter(description = "ID of the category to filter events")
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @Parameter(description = "Field to sort events")
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)")
            @RequestParam(name = "sortDirection", defaultValue = "ASC") SortDirection sortDirection,
            @Parameter(description = "Page number for pagination")
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        logger.info("Received getAllEvents - Date From: {}, Date To: {}, SemesterId: {}, Keyword: {}, CategoryId: {}, sortDirection: {}, sortBy: {}",
               dateFrom, dateTo, semesterId, keyword, categoryId, sortDirection, sortBy);

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new BadRequestException("Page number must be greater than 0", HttpStatus.BAD_REQUEST);
        }

        EventFilterDTO filterDTO = EventFilterDTO.builder()
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .semesterId(semesterId)
                .keyword(keyword)
                .categoryId(categoryId)
                .pagination(PageRequest.of(page - 1, 10))
                .sortDirection(sortDirection)
                .sortBy(sortBy)
                .build();

        PaginationDTO<List<EventDTO>> responseDTO = eventService.getAllEvents(filterDTO);

        logger.info("Successfully retrieved events - Total Events: {}", responseDTO.getTotalElements());

        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Get event by ID",
            description = "Fetches an event by its ID and records the interaction type based on the filter status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event fetched successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "404", description = "Event or Student not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<Object> getOneEvent(
            @PathVariable("eventId") Long eventId,
            @RequestParam(required = false) boolean isFilter,
            @NotNull @AuthenticationPrincipal Account principle) {

        EventDTO eventDTO = eventService.getOneEvent(eventId, principle.getId(), isFilter);
        return ResponseUtil.getResponse(eventDTO, HttpStatus.OK);
    }
}

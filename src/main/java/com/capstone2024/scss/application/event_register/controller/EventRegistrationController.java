package com.capstone2024.scss.application.event_register.controller;

import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.event_register.dto.StudentEventScheduleDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.event_register.service.EventRegistrationService;
import com.capstone2024.scss.domain.student.entities.Student;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/event-registering")
@RequiredArgsConstructor
@Tag(name = "Event Register", description = "API for register events")
public class EventRegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(EventRegistrationService.class);

    private final EventRegistrationService eventRegistrationService;

    @PostMapping("/{eventScheduleId}")
    public ResponseEntity<Object> registerForEvent(@PathVariable Long eventScheduleId,
                                                   @NotNull @AuthenticationPrincipal Account principle,
                                                   @RequestParam(name = "isForced", defaultValue = "false") boolean isForced) {
        eventRegistrationService.registerStudentForEvent((Student) principle.getProfile(), eventScheduleId, isForced);
        return ResponseUtil.getResponse("Register to event successfully", HttpStatus.OK);
    }

    @GetMapping("/student-schedule")
    public ResponseEntity<Object> getAllStudentSchedules(
            @NotNull @AuthenticationPrincipal Account principle,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        logger.info("API called to get student schedules for studentId: {}, from: {}, to: {}", principle.getProfile().getId(), from, to);
        List<StudentEventScheduleDTO> schedules = eventRegistrationService.getAllStudentSchedules(principle.getProfile().getId(), from, to);
        return ResponseUtil.getResponse(schedules, HttpStatus.OK);
    }
}

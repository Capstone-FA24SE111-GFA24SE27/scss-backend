package com.capstone2024.scss.application.event.semester.controller;

import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.event.semester.dto.SemesterDTO;
import com.capstone2024.scss.domain.event.service.SemesterService;
import com.capstone2024.scss.infrastructure.repositories.event.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/semesters")
@RequiredArgsConstructor
public class SemesterController {

    private final SemesterService semesterService;

    @GetMapping
    public ResponseEntity<Object> getAllSemesters() {
        List<SemesterDTO> semesters = semesterService.getAllSemesters();
        return ResponseUtil.getResponse(semesters, HttpStatus.OK);
    }
}

package com.capstone2024.scss.application.demand.controller;

import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.demand.dto.ProblemTagCountResponse;
import com.capstone2024.scss.domain.demand.service.CounselingDemandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final CounselingDemandService demandProblemTagService;

    @GetMapping("/problem-tags")
    public ResponseEntity<Object> getProblemTagsBySemester(
            @RequestParam("semesterName") String semesterName) {
        List<ProblemTagCountResponse> response = demandProblemTagService.getProblemTagsAndCountBySemester(semesterName);
        return ResponseUtil.getResponse(response, HttpStatus.OK);
    }
}


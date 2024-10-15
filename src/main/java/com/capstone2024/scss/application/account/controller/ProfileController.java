package com.capstone2024.scss.application.account.controller;

import com.capstone2024.scss.application.account.dto.*;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.services.ProfileService;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.domain.common.mapper.account.CounselorProfileMapper;
import com.capstone2024.scss.domain.common.mapper.student.StudentMapper;
import com.capstone2024.scss.domain.counselor.entities.AcademicCounselor;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.counselor.entities.NonAcademicCounselor;
import com.capstone2024.scss.domain.student.entities.Student;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "profile")
public class ProfileController {

    private final ProfileService profileService;
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping()
    @Operation(summary = "Get profile by account ID")
    @ApiResponse(
            responseCode = "200",
            description = "Profile found and returned.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProfileDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Profile not found.",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "Server error.",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    public ResponseEntity<Object> getProfile(@AuthenticationPrincipal @NotNull Account principal) {
        logger.info("Fetching profile for account ID: {}", principal.getId());
        switch (principal.getRole()) {
            case STUDENT -> {
                Student student = (Student) principal.getProfile();
                StudentProfileDTO studentProfileDTO = StudentMapper.toStudentProfileDTO(student);
                return ResponseUtil.getResponse(studentProfileDTO, HttpStatus.OK);
            }
            case NON_ACADEMIC_COUNSELOR -> {
                Counselor counselor = (Counselor) principal.getProfile();
                NonAcademicCounselorProfileDTO counselorProfileDTO = CounselorProfileMapper.toNonAcademicCounselorProfileDTO((NonAcademicCounselor) counselor);
                return ResponseUtil.getResponse(counselorProfileDTO, HttpStatus.OK);
            }
            case ACADEMIC_COUNSELOR -> {
                Counselor counselor = (Counselor) principal.getProfile();
                AcademicCounselorProfileDTO counselorProfileDTO = CounselorProfileMapper.toAcademicCounselorProfileDTO((AcademicCounselor) counselor);
                return ResponseUtil.getResponse(counselorProfileDTO, HttpStatus.OK);
            }
            default -> {
                ProfileDTO profileDTO = profileService.getProfileByAccountId(principal.getId());
                return ResponseUtil.getResponse(profileDTO, HttpStatus.OK);
            }
        }
    }
}

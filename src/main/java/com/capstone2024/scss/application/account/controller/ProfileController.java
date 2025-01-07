package com.capstone2024.scss.application.account.controller;

import com.capstone2024.scss.application.account.dto.*;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.entities.Profile;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @PutMapping("/{profileId}/avatar")
    public ResponseEntity<?> updateAvatar(@PathVariable Long profileId, @RequestParam("file") MultipartFile file) throws IOException {

        return ResponseEntity.ok(profileService.updateAvatar(profileId, file));
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
//        switch (principal.getRole()) {
//            case STUDENT -> {
//                Student student = (Student) principal.getProfile();
//                StudentProfileDTO studentProfileDTO = StudentMapper.toStudentProfileDTO(student);
//                return ResponseUtil.getResponse(studentProfileDTO, HttpStatus.OK);
//            }
//            case NON_ACADEMIC_COUNSELOR -> {
//                Counselor counselor = (Counselor) principal.getProfile();
//                NonAcademicCounselorProfileDTO counselorProfileDTO = CounselorProfileMapper.toNonAcademicCounselorProfileDTO((NonAcademicCounselor) counselor);
//                return ResponseUtil.getResponse(counselorProfileDTO, HttpStatus.OK);
//            }
//            case ACADEMIC_COUNSELOR -> {
//                Counselor counselor = (Counselor) principal.getProfile();
//                AcademicCounselorProfileDTO counselorProfileDTO = CounselorProfileMapper.toAcademicCounselorProfileDTO((AcademicCounselor) counselor);
//                return ResponseUtil.getResponse(counselorProfileDTO, HttpStatus.OK);
//            }
//            default -> {
//                ProfileDTO profileDTO = profileService.getProfileByAccountId(principal.getId());
//                return ResponseUtil.getResponse(profileDTO, HttpStatus.OK);
//            }
//        }
        return ResponseUtil.getResponse(profileService.getProfileByAccountIdForEachRole(principal.getId()), HttpStatus.OK);
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get profile by account ID")
    public ResponseEntity<Object> getProfileForManage(@PathVariable Long accountId) {
        return ResponseUtil.getResponse(profileService.getProfileByAccountIdForEachRole(accountId), HttpStatus.OK);
    }
}

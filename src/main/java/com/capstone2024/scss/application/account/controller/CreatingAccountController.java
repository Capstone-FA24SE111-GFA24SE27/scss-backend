package com.capstone2024.scss.application.account.controller;

import com.capstone2024.scss.application.account.dto.create_account.AcademicCounselorAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.ManagerAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.NonAcademicCounselorAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.SupportStaffAccountDTO;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.counselor.dto.CertificationDTO;
import com.capstone2024.scss.application.counselor.dto.QualificationDTO;
import com.capstone2024.scss.domain.account.services.ManipulatingAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account/create")
@Tag(name = "account create", description = "API for managing user accounts.")
@RequiredArgsConstructor
public class CreatingAccountController {
    private static final Logger logger = LoggerFactory.getLogger(CreatingAccountController.class);
    private final ManipulatingAccountService manipulatingAccountService;

    @PostMapping("/manager")
    public ResponseEntity<Object> createManagerAccount(@Valid @RequestBody ManagerAccountDTO managerAccountDTO,
                                                       BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("Login request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid login request", errors, HttpStatus.BAD_REQUEST);
        }
        manipulatingAccountService.createManagerAccount(managerAccountDTO);
        return ResponseUtil.getResponse("Manager account created successfully.", HttpStatus.OK);
    }

    @PostMapping("/support-staff")
    public ResponseEntity<Object> createSupportStaffAccount(@Valid @RequestBody SupportStaffAccountDTO supportStaffAccountDTO,
                                                            BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("Support staff creation request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid request", errors, HttpStatus.BAD_REQUEST);
        }
        manipulatingAccountService.createSupportStaffAccount(supportStaffAccountDTO);
        return ResponseUtil.getResponse("Support staff account created successfully.", HttpStatus.OK);
    }

    @PostMapping("/academic-counselor")
    public ResponseEntity<Object> createAcademicCounselorAccount(@Valid @RequestBody AcademicCounselorAccountDTO dto,
                                                                 BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("Academic counselor creation request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid request", errors, HttpStatus.BAD_REQUEST);
        }
        manipulatingAccountService.createAcademicCounselorAccount(dto);
        return ResponseUtil.getResponse("Academic counselor account created successfully.", HttpStatus.OK);
    }

    @PostMapping("/non-academic-counselor")
    public ResponseEntity<Object> createNonAcademicCounselorAccount(@Valid @RequestBody NonAcademicCounselorAccountDTO dto,
                                                                 BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("Academic counselor creation request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid request", errors, HttpStatus.BAD_REQUEST);
        }
        manipulatingAccountService.createNonAcademicCounselorAccount(dto);
        return ResponseUtil.getResponse("Academic counselor account created successfully.", HttpStatus.OK);
    }

    @PutMapping("/manager")
    public ResponseEntity<Object> updateManagerAccount(@Valid @RequestBody ManagerAccountDTO managerAccountDTO,
                                                       BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("Manager account update request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid request", errors, HttpStatus.BAD_REQUEST);
        }
        manipulatingAccountService.updateManagerAccount(managerAccountDTO);
        return ResponseUtil.getResponse("Manager account updated successfully.", HttpStatus.OK);
    }

    @PutMapping("/support-staff")
    public ResponseEntity<Object> updateSupportStaffAccount(@Valid @RequestBody SupportStaffAccountDTO supportStaffAccountDTO,
                                                            BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("Support staff update request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid request", errors, HttpStatus.BAD_REQUEST);
        }
        manipulatingAccountService.updateSupportStaffAccount(supportStaffAccountDTO);
        return ResponseUtil.getResponse("Support staff account updated successfully.", HttpStatus.OK);
    }

    @PutMapping("/academic-counselor")
    public ResponseEntity<Object> updateAcademicCounselorAccount(@Valid @RequestBody AcademicCounselorAccountDTO dto,
                                                                 BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("Academic counselor update request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid request", errors, HttpStatus.BAD_REQUEST);
        }
        manipulatingAccountService.updateAcademicCounselorAccount(dto);
        return ResponseUtil.getResponse("Academic counselor account updated successfully.", HttpStatus.OK);
    }

    @PutMapping("/non-academic-counselor")
    public ResponseEntity<Object> updateNonAcademicCounselorAccount(@Valid @RequestBody NonAcademicCounselorAccountDTO dto,
                                                                    BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("Non-academic counselor update request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid request", errors, HttpStatus.BAD_REQUEST);
        }
        manipulatingAccountService.updateNonAcademicCounselorAccount(dto);
        return ResponseUtil.getResponse("Non-academic counselor account updated successfully.", HttpStatus.OK);
    }

    @PostMapping("/academic-counselor/{id}/qualification")
    public ResponseEntity<Object> addQualification(
            @PathVariable Long id,
            @Valid @RequestBody QualificationDTO qualificationDTO,
            BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("Add qualification request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid qualification request", errors, HttpStatus.BAD_REQUEST);
        }
        manipulatingAccountService.addQualification(id, qualificationDTO);
        return ResponseUtil.getResponse("Qualification added successfully.", HttpStatus.OK);
    }

    @PostMapping("/academic-counselor/{id}/certification")
    public ResponseEntity<Object> addCertification(
            @PathVariable Long id,
            @Valid @RequestBody CertificationDTO certificationDTO,
            BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("Add certification request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid certification request", errors, HttpStatus.BAD_REQUEST);
        }
        manipulatingAccountService.addCertification(id, certificationDTO);
        return ResponseUtil.getResponse("Certification added successfully.", HttpStatus.OK);
    }

    @DeleteMapping("/counselor/{id}/qualification/{qualificationId}")
    public ResponseEntity<Object> deleteQualification(
            @PathVariable Long id,
            @PathVariable Long qualificationId) {
        manipulatingAccountService.deleteQualification(id, qualificationId);
        return ResponseUtil.getResponse("Qualification deleted successfully.", HttpStatus.OK);
    }

    // Delete Certification
    @DeleteMapping("/counselor/{id}/certification/{certificationId}")
    public ResponseEntity<Object> deleteCertification(
            @PathVariable Long id,
            @PathVariable Long certificationId) {
        manipulatingAccountService.deleteCertification(id, certificationId);
        return ResponseUtil.getResponse("Certification deleted successfully.", HttpStatus.OK);
    }

    @PutMapping("/counselor/{id}/qualification/{qualificationId}")
    public ResponseEntity<Object> updateQualification(
            @PathVariable Long id,
            @PathVariable Long qualificationId,
            @Valid @RequestBody QualificationDTO qualificationDTO,
            BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("Update qualification request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid qualification update request", errors, HttpStatus.BAD_REQUEST);
        }
        manipulatingAccountService.updateQualification(id, qualificationId, qualificationDTO);
        return ResponseUtil.getResponse("Qualification updated successfully.", HttpStatus.OK);
    }

    // Update Certification
    @PutMapping("/counselor/{id}/certification/{certificationId}")
    public ResponseEntity<Object> updateCertification(
            @PathVariable Long id,
            @PathVariable Long certificationId,
            @Valid @RequestBody CertificationDTO certificationDTO,
            BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("Update certification request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid certification update request", errors, HttpStatus.BAD_REQUEST);
        }
        manipulatingAccountService.updateCertification(id, certificationId, certificationDTO);
        return ResponseUtil.getResponse("Certification updated successfully.", HttpStatus.OK);
    }

}

package com.capstone2024.scss.application.account.controller;

import com.capstone2024.scss.application.account.dto.create_account.AcademicCounselorAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.ManagerAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.NonAcademicCounselorAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.SupportStaffAccountDTO;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.domain.account.services.ManipulatingAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

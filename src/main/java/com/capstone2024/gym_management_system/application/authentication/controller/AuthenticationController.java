package com.capstone2024.gym_management_system.application.authentication.controller;

import com.capstone2024.gym_management_system.application.advice.exeptions.NotFoundException;
import com.capstone2024.gym_management_system.application.advice.exeptions.BadRequestException;
import com.capstone2024.gym_management_system.application.authentication.dto.JwtTokenDTO;
import com.capstone2024.gym_management_system.application.authentication.dto.request.LoginRequestDTO;
import com.capstone2024.gym_management_system.application.common.utils.ResponseUtil;
import com.capstone2024.gym_management_system.domain.account.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login an account")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Login credential",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginRequestDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "Logged in successfully and returns a JWT token.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JwtTokenDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request body.",
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
    //@SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO, BindingResult errors, HttpServletResponse response) {
        if (errors.hasErrors()) {
            logger.warn("Login request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("", errors, HttpStatus.BAD_REQUEST);
        }
        logger.info("Processing login request for email: {}", loginRequestDTO.getEmail());
        JwtTokenDTO responseDTO = authenticationService.login(loginRequestDTO, response);

        logger.info("Login successful for email: {}", loginRequestDTO.getEmail());
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<Object> getNewAccessToken(HttpServletResponse response, HttpServletRequest request) {
        JwtTokenDTO responseDTO = authenticationService.refreshAccessToken(response, request);

        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }
}

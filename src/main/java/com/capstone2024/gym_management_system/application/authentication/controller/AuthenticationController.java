package com.capstone2024.gym_management_system.application.authentication.controller;

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

@RestController
@RequestMapping("/api/auth")
@Tag(name = "authentication", description = "Authentication API for login, token refresh, and OAuth login.")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login/default")
    @Operation(
            summary = "Login with email and password",
            description = "Authenticates a user with email and password, and returns a JWT token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully logged in, JWT token returned.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JwtTokenDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body.",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Server error.",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            }
    )
    public ResponseEntity<Object> loginWithDefault(@Valid @RequestBody LoginRequestDTO loginRequestDTO, BindingResult errors, HttpServletResponse response) {
        if (errors.hasErrors()) {
            logger.warn("Login request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid login request", errors, HttpStatus.BAD_REQUEST);
        }

        logger.info("Processing login request for email: {}", loginRequestDTO.getEmail());
        JwtTokenDTO responseDTO = authenticationService.loginWithDefault(loginRequestDTO, response);

        logger.info("Login successful for email: {}", loginRequestDTO.getEmail());
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/login/oauth/google/{access_token}")
    @Operation(
            summary = "Login with Google OAuth",
            description = "Authenticates a user using Google OAuth access token, and returns a JWT token.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully logged in with Google OAuth, JWT token returned.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JwtTokenDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid access token.",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Server error.",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            }
    )
    public ResponseEntity<Object> loginWithGoogle(@PathVariable("access_token") String accessToken) {
        logger.info("Processing Google OAuth login with access token.");
        JwtTokenDTO responseDTO = authenticationService.loginWithGoogle(accessToken);
        logger.info("Google OAuth login successful.");
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/refresh-token")
    @Operation(
            summary = "Refresh access token",
            description = "Refreshes the access token using the refresh token from the request.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully refreshed access token, new JWT token returned.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JwtTokenDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request or token.",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Server error.",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            }
    )
    public ResponseEntity<Object> getNewAccessToken(HttpServletResponse response, HttpServletRequest request) {
        logger.info("Refreshing access token.");
        JwtTokenDTO responseDTO = authenticationService.refreshAccessToken(response, request);
        logger.info("Access token successfully refreshed.");
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }
}

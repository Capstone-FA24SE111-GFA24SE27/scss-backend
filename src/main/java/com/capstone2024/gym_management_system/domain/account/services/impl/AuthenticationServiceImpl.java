package com.capstone2024.gym_management_system.domain.account.services.impl;

import com.capstone2024.gym_management_system.application.advice.exeptions.BadRequestException;
import com.capstone2024.gym_management_system.application.advice.exeptions.NotFoundException;
import com.capstone2024.gym_management_system.application.authentication.dto.request.LoginRequestDTO;
import com.capstone2024.gym_management_system.application.authentication.dto.JwtTokenDTO;
import com.capstone2024.gym_management_system.domain.account.entities.Account;
import com.capstone2024.gym_management_system.domain.account.entities.LoginType;
import com.capstone2024.gym_management_system.domain.account.entities.Profile;
import com.capstone2024.gym_management_system.domain.account.enums.LoginMethod;
import com.capstone2024.gym_management_system.domain.account.enums.Role;
import com.capstone2024.gym_management_system.domain.account.enums.Status;
import com.capstone2024.gym_management_system.domain.account.services.AuthenticationService;
import com.capstone2024.gym_management_system.infrastructure.configuration.security.authentication.JwtService;
import com.capstone2024.gym_management_system.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.gym_management_system.infrastructure.repositories.account.LoginTypeRepository;
import com.capstone2024.gym_management_system.infrastructure.repositories.account.ProfileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private static final String ACCESS_TOKEN_TYPE = "BEARER";
    private static final String COOKIE_REFRESH_TOKEN_KEY = "refreshToken";

    @Value("${jwt.refresh_token.lifetime}")
    private Long refreshTokenLifetime;

    private final AccountRepository accountRepository;
    private final LoginTypeRepository loginTypeRepository;
    private final ProfileRepository profileRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;

    public AuthenticationServiceImpl(AccountRepository accountRepository, LoginTypeRepository loginTypeRepository, ProfileRepository profileRepository, JwtService jwtService, AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        this.accountRepository = accountRepository;
        this.loginTypeRepository = loginTypeRepository;
        this.profileRepository = profileRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public JwtTokenDTO loginWithDefault(LoginRequestDTO loginRequestDTO, HttpServletResponse response) throws NotFoundException {
        logger.info("Attempting to log in with email: {}", loginRequestDTO.getEmail());

        UserDetails userDetails = getAccount(loginRequestDTO.getEmail());

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));
        } catch (Exception e) {
            logger.error("Authentication failed for email: {}", loginRequestDTO.getEmail(), e);
            throw new BadRequestException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        String accessToken = jwtService.generateToken(userDetails);
        logger.info("Login successful for email: {}. Access token generated.", loginRequestDTO.getEmail());

        setRefreshTokenInCookie(userDetails, response);
        logger.info("Refresh token generated and added to cookie for email: {}.", loginRequestDTO.getEmail());

        return JwtTokenDTO.builder()
                .accessToken(accessToken)
                .type(ACCESS_TOKEN_TYPE)
                .build();
    }

    @Override
    public JwtTokenDTO refreshAccessToken(HttpServletResponse response, HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            logger.warn("Refresh token not found in cookies.");
            throw new BadRequestException("Refresh token not found", HttpStatus.BAD_REQUEST);
        }

        String email = jwtService.validateJwtToken(refreshToken);
        if (email == null) {
            logger.warn("Invalid refresh token provided.");
            throw new BadRequestException("Invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        logger.info("Attempting to find account with email: {}", email);

        UserDetails userDetails = getAccount(email);

        String accessToken = jwtService.generateToken(userDetails);
        logger.info("Access token refreshed for email: {}. New access token generated.", email);

        return JwtTokenDTO.builder()
                .accessToken(accessToken)
                .type(ACCESS_TOKEN_TYPE)
                .build();
    }

    @Override
    public JwtTokenDTO loginWithGoogle(String googleAccessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // URL để xác thực mã thông báo Google
        String url = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + googleAccessToken;

        // Lấy thông tin người dùng từ Google
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            throw new BadRequestException("Invalid Google Token", HttpStatus.BAD_REQUEST);
        }

        String email = jsonNode.get("email").asText();

        if(email == null || !email.endsWith("@fpt.edu.vn")) {
            throw new BadRequestException("Only FPT accounts are allowed", HttpStatus.BAD_REQUEST);
        }

        String name = jsonNode.get("name").asText();
        logger.info("Attempting to find account with email: {}", email);

        Optional<Account> account = accountRepository
                .findAccountByEmail(email);

        Account userDetails;

        if(account.isEmpty()) {
            logger.info("Account not found for email: {}. Creating a new account.", email);

            Account newAccountWithGoogle = Account.builder()
                    .email(email)
                    .role(Role.ADMINISTRATOR)
                    .status(Status.ACTIVE)
                    .build();

            userDetails = accountRepository.save(newAccountWithGoogle);

            LoginType adminLoginType = LoginType.builder()
                    .method(LoginMethod.GOOGLE)
                    .account(newAccountWithGoogle)
                    .build();

            loginTypeRepository.save(adminLoginType);

            // Create and save Profile for the newAccountWithGoogle account
            Profile newProfile = Profile.builder()
                    .account(newAccountWithGoogle)
                    .fullName(name)
                    .phoneNumber(null)
                    .address(null)
                    .dateOfBirth(null)
                    .build();

            profileRepository.save(newProfile);

            logger.info("New account and profile created for email: {}", email);
        } else {
            userDetails = account.get();
            LoginType adminLoginType = LoginType.builder()
                    .method(LoginMethod.GOOGLE)
                    .account(userDetails)
                    .build();
            loginTypeRepository.save(adminLoginType);
        }

        String accessToken = jwtService.generateToken(userDetails);
        logger.info("Access token refreshed for email: {}. New access token generated.", email);

        return JwtTokenDTO.builder()
                .accessToken(accessToken)
                .type(ACCESS_TOKEN_TYPE)
                .build();
    }

    private UserDetails getAccount(String email) {
        UserDetails userDetails = accountRepository
                .findAccountByEmail(email)
                .orElse(null);

        if (Objects.isNull(userDetails)) {
            logger.warn("Account not found for email: {}", email);
            throw new NotFoundException(String.format("Account with email \"%s\" not found.",
                    email), HttpStatus.NOT_FOUND);
        }

        return userDetails;
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_REFRESH_TOKEN_KEY.equals(cookie.getName())) {
                    logger.debug("Refresh token found in cookie.");
                    return cookie.getValue();
                }
            }
        }

        logger.debug("Refresh token not found in cookies.");
        return null;
    }

    private void setRefreshTokenInCookie(UserDetails userDetails, HttpServletResponse response) {
        String refreshToken = jwtService.generateToken(userDetails, refreshTokenLifetime);

        Cookie cookie = new Cookie(COOKIE_REFRESH_TOKEN_KEY, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenLifetime.intValue());

        response.addCookie(cookie);

        logger.debug("Refresh token set in cookie with expiration: {} seconds.", refreshTokenLifetime);
    }
}

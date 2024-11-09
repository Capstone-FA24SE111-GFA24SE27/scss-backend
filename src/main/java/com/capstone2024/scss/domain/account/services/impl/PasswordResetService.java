package com.capstone2024.scss.domain.account.services.impl;

import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.RabbitMQConfig;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto.PasswordResetEmail;
import com.capstone2024.scss.infrastructure.configuration.redis.RedisService;
import com.capstone2024.scss.infrastructure.repositories.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j // Enables SLF4J logging
public class PasswordResetService {

    private static final long TOKEN_EXPIRATION_MINUTES = 15; // Token expiration time in Redis (15 minutes)
    private final RedisService redisService;
    private final RabbitTemplate rabbitTemplate;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url}")
    private String appBaseUrl;

    public String generateResetLink(String token) {
        return appBaseUrl + "/api/account/reset-password?token=" + token;
    }

    // Handles the forgot password request
    public void handleForgotPassword(String email) {
        // Generate a unique token (UUID)
        String token = UUID.randomUUID().toString();
        String redisKey = "password_reset_token:" + token;

        // Store the token in Redis with an expiration time
        redisService.saveDataWithExpiration(redisKey, email, TOKEN_EXPIRATION_MINUTES, TimeUnit.MINUTES);
        log.info("Generated password reset token for email: {} with expiration of {} minutes", email, TOKEN_EXPIRATION_MINUTES);

        // Create the password reset link
        String resetLink = generateResetLink(token);
        log.info("Generated reset link for email: {}: {}", email, resetLink);

        // Send the reset link via RabbitMQ
        PasswordResetEmail emailData = new PasswordResetEmail(email, "Password Reset Request", resetLink);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, emailData);
        log.info("Password reset link sent to {} via RabbitMQ", email);
    }

    // Handles password reset using the token
    public void handlePasswordReset(String token) {
        // Retrieve email from Redis based on the token
        String email = (String) redisService.getData("password_reset_token:" + token);
        if (email != null) {
            log.info("Password reset token validated for email: {}", email);

            // Generate a new random password
            String newPassword = UUID.randomUUID().toString().substring(0, 8);

            // Send the new password via email
            PasswordResetEmail emailData = new PasswordResetEmail(email, "Your New Password", "Your new password is: " + newPassword);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, emailData);
            log.info("New password sent to {} via RabbitMQ", email);

            // Update the password in the database
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found"));
            account.setPassword(passwordEncoder.encode(newPassword));
            accountRepository.save(account);
            log.info("Password updated for account with email: {}", email);

            // Remove the token from Redis after successful reset
            redisService.deleteData("password_reset_token:" + token);
            log.info("Password reset token deleted from Redis for email: {}", email);
        } else {
            log.warn("Invalid or expired password reset token: {}", token);
            throw new BadRequestException("Invalid or expired token");
        }
    }
}

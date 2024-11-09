package com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordResetEmail {
    private String recipient;
    private String subject;
    private String content;
}

package com.capstone2024.gym_management_system.application.authentication.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokenDTO {
    private String accessToken;
    private String type;
    private AccountDTO account;
}

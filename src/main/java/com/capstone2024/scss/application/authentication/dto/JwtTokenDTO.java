package com.capstone2024.scss.application.authentication.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokenDTO {
    private String accessToken;
    private String refreshToken;
    private String type;
    private AccountDTO account;
}

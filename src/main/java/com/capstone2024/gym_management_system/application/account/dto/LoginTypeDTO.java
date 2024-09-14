package com.capstone2024.gym_management_system.application.account.dto;

import com.capstone2024.gym_management_system.domain.account.enums.LoginMethod;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginTypeDTO {
    private LoginMethod method;
    private String password;
}

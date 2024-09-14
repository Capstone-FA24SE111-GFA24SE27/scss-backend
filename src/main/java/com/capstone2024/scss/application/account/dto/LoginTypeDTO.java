package com.capstone2024.scss.application.account.dto;

import com.capstone2024.scss.domain.account.enums.LoginMethod;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginTypeDTO {
    private LoginMethod method;
    private String password;
}

package com.capstone2024.scss.application.account.dto.request;

import com.capstone2024.scss.domain.account.enums.LoginMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginTypeRequest {
    private LoginMethod method;

    @NotBlank(message = "Password must not be blank or null.")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters.")
    private String password;

//    @NotBlank(message = "Password must not be blank or null.")
//    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters.")
//    private String rePassword;
}

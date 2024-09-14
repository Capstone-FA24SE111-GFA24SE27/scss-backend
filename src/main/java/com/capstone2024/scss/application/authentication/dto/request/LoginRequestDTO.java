package com.capstone2024.scss.application.authentication.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "Email must not be blank or null.")
    @Email(message = "Email must have valid structure.")
    @Size(min = 3, max = 255, message = "Email must be between 3 and 255 characters.")
    String email;

    @NotBlank(message = "Password must not be blank or null.")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters.")
    String password;
}

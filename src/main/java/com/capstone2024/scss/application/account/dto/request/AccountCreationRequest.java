package com.capstone2024.scss.application.account.dto.request;

import com.capstone2024.scss.application.account.dto.LoginTypeDTO;
import com.capstone2024.scss.application.account.dto.ProfileDTO;
import com.capstone2024.scss.domain.account.enums.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreationRequest {

    @NotBlank(message = "Email must not be blank or null.")
    @Email(message = "Email must have valid structure.")
    @Size(min = 3, max = 255, message = "Email must be between 3 and 255 characters.")
    private String email;

    @NotNull(message = "Password must not be blank or null.")
    @Valid
    private LoginTypeRequest login;

    @NotNull(message = "Password must not be blank or null.")
    @Valid
    private ProfileDTO profile;

    private Role role;
}

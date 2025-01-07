package com.capstone2024.scss.application.account.dto.create_account;

import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ManagerAccountDTO {

    private Long id;

    private String avatarLink;

//    @NotEmpty(message = "Email is required")
//    @Email(message = "Invalid email format")
    private String email;

//    @NotEmpty(message = "Password is required")
    private String password;

    @NotEmpty(message = "Full name is required")
    private String fullName;

    @NotEmpty(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;
}


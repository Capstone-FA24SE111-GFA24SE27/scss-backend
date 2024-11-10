package com.capstone2024.scss.application.account.dto.create_account;

import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateDTO {
    private String fullName;
    private Gender gender;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
}

package com.capstone2024.gym_management_system.application.profile.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String address;
    private Long dateOfBirth;
}

package com.capstone2024.gym_management_system.application.profile.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String address;
    private Long dateOfBirth;
}

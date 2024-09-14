package com.capstone2024.gym_management_system.application.counselor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounselorDTO {

    private Long id;
    private String email;
    private String avatarLink;
    private BigDecimal rating;
    private String fullName;
    private String phoneNumber;
    private Long dateOfBirth;
}

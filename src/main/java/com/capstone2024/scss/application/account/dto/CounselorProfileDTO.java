package com.capstone2024.scss.application.account.dto;

import com.capstone2024.scss.domain.common.mapper.appointment_counseling.ExpertiseDTO;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounselorProfileDTO {
    private Long id;

    private ProfileDTO profile;

    private BigDecimal rating;

    private String email;

    private Gender gender;

    private ExpertiseDTO expertise;
}

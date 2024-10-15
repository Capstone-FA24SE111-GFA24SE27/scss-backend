package com.capstone2024.scss.application.account.dto;

import com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CounselorProfileDTO {
    private Long id;

    private ProfileDTO profile;

    private BigDecimal rating;

    private String email;

    private Gender gender;

    private CounselorStatus status;
}

package com.capstone2024.scss.application.account.dto;

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
}

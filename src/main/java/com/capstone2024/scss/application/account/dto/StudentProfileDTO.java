package com.capstone2024.scss.application.account.dto;

import com.capstone2024.scss.application.counselor.dto.SpecializationDTO;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDTO {
    private Long id;

    private ProfileDTO profile;

    private String studentCode;

    private String email;

    private SpecializationDTO specialization;
}

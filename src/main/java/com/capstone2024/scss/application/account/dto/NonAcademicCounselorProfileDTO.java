package com.capstone2024.scss.application.account.dto;

import com.capstone2024.scss.application.counselor.dto.ExpertiseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NonAcademicCounselorProfileDTO extends CounselorProfileDTO {
    private ExpertiseDTO expertise;
    private Integer industryExperience;
}

package com.capstone2024.scss.application.account.dto;

import com.capstone2024.scss.application.counselor.dto.SpecializationDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicCounselorProfileDTO extends CounselorProfileDTO {
    private SpecializationDTO specialization; // Specific field for Academic Counselors
    private String academicDegree;  // Specific field for Academic Counselors
}

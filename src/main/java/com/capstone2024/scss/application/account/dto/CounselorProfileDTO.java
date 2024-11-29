package com.capstone2024.scss.application.account.dto;

import com.capstone2024.scss.application.counselor.dto.CertificationDTO;
import com.capstone2024.scss.application.counselor.dto.QualificationDTO;
import com.capstone2024.scss.domain.counselor.entities.Certification;
import com.capstone2024.scss.domain.counselor.entities.Qualification;
import com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

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

    private String specializedSkills;

    private String otherSkills;

    private String workHistory;

    private String achievements;

    private List<QualificationDTO> qualifications;

    private List<CertificationDTO> certifications;
}

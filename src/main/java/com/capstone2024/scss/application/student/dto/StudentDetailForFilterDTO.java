package com.capstone2024.scss.application.student.dto;

import com.capstone2024.scss.application.account.dto.ProfileDTO;
import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.common.dto.DepartmentDTO;
import com.capstone2024.scss.application.common.dto.MajorDTO;
import com.capstone2024.scss.application.common.dto.SpecializationDTO;
import com.capstone2024.scss.infrastructure.data.fap.dto.DemandProblemTagFapResponseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetailForFilterDTO {
    private Long id;

    private ProfileDTO profile;

    private String studentCode;

    private String email;

    private SpecializationDTO specialization;

    private DepartmentDTO department;

    private MajorDTO major;

    private List<DemandProblemTagResponseDTO> behaviorTagList;
}

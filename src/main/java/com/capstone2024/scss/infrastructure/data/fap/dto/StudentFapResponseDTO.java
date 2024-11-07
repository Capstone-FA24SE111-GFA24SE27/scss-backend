package com.capstone2024.scss.infrastructure.data.fap.dto;

import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentFapResponseDTO {
    private String studentCode;
    private String batch;
    private String email;
    private String fullName;
    private String majorName;
    private int currentTerm;
    private String departmentName;
    private String specializationName;
    private Gender gender;
}

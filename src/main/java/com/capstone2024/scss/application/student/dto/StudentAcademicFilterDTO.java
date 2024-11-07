package com.capstone2024.scss.application.student.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentAcademicFilterDTO {
    private Long specializationId;
    private Long departmentId;
    private Long majorId;
    private Integer currentTerm;
}

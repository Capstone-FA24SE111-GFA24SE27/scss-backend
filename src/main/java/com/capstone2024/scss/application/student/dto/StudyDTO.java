package com.capstone2024.scss.application.student.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyDTO {
    private String subjectCode;
    private String subjectName;
    private Integer term;
    private BigDecimal grade;
    private String status;
    private String semester;
}

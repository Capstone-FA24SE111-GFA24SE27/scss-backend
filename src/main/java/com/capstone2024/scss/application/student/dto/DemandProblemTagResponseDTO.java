package com.capstone2024.scss.application.student.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandProblemTagResponseDTO {
    private Long id;              // ID của DemandProblemTag
    private String studentCode;        // ID của Student
    private String source;         // Nguồn gốc
    private String problemTagName;     // ID của ProblemTag
    private int number;            // Số lượng
    private String semesterName;
    private String category;
    private boolean isContained;
    private boolean isExcluded;
}

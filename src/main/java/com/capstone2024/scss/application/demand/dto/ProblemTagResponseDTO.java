package com.capstone2024.scss.application.demand.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemTagResponseDTO {
    private Long id;
    private String name;
    private int point;
    private ProblemCategoryResponseDTO category;
}
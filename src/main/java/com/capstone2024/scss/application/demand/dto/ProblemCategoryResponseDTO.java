package com.capstone2024.scss.application.demand.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ProblemCategoryResponseDTO {
    private Long id;
    private String name;
}

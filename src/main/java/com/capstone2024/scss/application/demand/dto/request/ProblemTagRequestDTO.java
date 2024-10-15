package com.capstone2024.scss.application.demand.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProblemTagRequestDTO {
    private String name;
    private int point;
    private Long categoryId;
}

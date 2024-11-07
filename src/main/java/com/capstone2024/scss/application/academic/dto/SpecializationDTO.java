package com.capstone2024.scss.application.academic.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SpecializationDTO {
    private Long id;
    private String name;
    private String code;
}


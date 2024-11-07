package com.capstone2024.scss.application.student.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StudentGPAFilterDTO {
    private Long semesterId;
    private BigDecimal min;
    private BigDecimal max;
}

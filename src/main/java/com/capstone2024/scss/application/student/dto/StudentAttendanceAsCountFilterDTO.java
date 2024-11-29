package com.capstone2024.scss.application.student.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StudentAttendanceAsCountFilterDTO {
    private Long semesterId;
    private Integer minSubject;
    private Integer from;
    private Integer to;
}

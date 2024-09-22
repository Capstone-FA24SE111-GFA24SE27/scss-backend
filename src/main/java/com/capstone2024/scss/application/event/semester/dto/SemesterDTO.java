package com.capstone2024.scss.application.event.semester.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class SemesterDTO {
    private Long id;
    private String semesterCode;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}

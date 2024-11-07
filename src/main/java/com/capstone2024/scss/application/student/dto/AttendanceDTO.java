package com.capstone2024.scss.application.student.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class AttendanceDTO {
    private Long id;
    private LocalDate startDate;
    private int totalSlot;
    private String studentCode;
    private String subjectName;
    private String semesterName;
    private List<AttendanceDetailDTO> detais;
}

package com.capstone2024.scss.application.student.dto;

import com.capstone2024.scss.application.student.dto.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class AttendanceDetailDTO {
    private LocalDate date;
    private String slot;
    private String room;
    private String lecturer;
    private String groupName;
    private AttendanceStatus status;
    private String lecturerComment;
}

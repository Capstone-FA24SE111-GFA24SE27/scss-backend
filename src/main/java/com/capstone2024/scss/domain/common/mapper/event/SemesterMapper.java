package com.capstone2024.scss.domain.common.mapper.event;

import com.capstone2024.scss.application.event.semester.dto.SemesterDTO;
import com.capstone2024.scss.domain.event.entities.Semester;

public class SemesterMapper {
    public static SemesterDTO toDTO(Semester semester) {
        if (semester == null) {
            return null;
        }

        return SemesterDTO.builder()
                .id(semester.getId())
                .semesterCode(semester.getSemesterCode())
                .name(semester.getName())
                .startDate(semester.getStartDate())
                .endDate(semester.getEndDate())
                .build();
    }
}

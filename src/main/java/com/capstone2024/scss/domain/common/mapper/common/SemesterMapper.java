package com.capstone2024.scss.domain.common.mapper.common;

import com.capstone2024.scss.application.common.dto.SemesterDTO;
import com.capstone2024.scss.domain.common.entity.Semester;

public class SemesterMapper {
    public static SemesterDTO toSemesterDTO(Semester semester) {
        if (semester == null) {
            return null;
        }

        return SemesterDTO.builder()
                .id(semester.getId())
                .name(semester.getName())
                .build();
    }
}

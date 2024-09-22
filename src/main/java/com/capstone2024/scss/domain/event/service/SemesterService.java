package com.capstone2024.scss.domain.event.service;

import com.capstone2024.scss.application.event.semester.dto.SemesterDTO;

import java.util.List;

public interface SemesterService {
    List<SemesterDTO> getAllSemesters();
}

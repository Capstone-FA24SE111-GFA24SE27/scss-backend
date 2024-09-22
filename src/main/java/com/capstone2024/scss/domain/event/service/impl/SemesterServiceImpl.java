package com.capstone2024.scss.domain.event.service.impl;

import com.capstone2024.scss.application.event.semester.dto.SemesterDTO;
import com.capstone2024.scss.domain.event.entities.Semester;
import com.capstone2024.scss.domain.event.service.SemesterService;
import com.capstone2024.scss.infrastructure.repositories.event.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SemesterServiceImpl implements SemesterService {
    private final SemesterRepository semesterRepository;

    @Override
    public List<SemesterDTO> getAllSemesters() {
        List<Semester> semesters = semesterRepository.findAll();
        return semesters.stream()
                .map(semester -> SemesterDTO.builder()
                        .id(semester.getId())
                        .semesterCode(semester.getSemesterCode())
                        .name(semester.getName())
                        .startDate(semester.getStartDate())
                        .endDate(semester.getEndDate())
                        .build())
                .collect(Collectors.toList());
    }
}

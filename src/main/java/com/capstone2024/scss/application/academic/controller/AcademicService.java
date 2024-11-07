package com.capstone2024.scss.application.academic.controller;

import com.capstone2024.scss.application.common.dto.DepartmentDTO;
import com.capstone2024.scss.application.common.dto.MajorDTO;
import com.capstone2024.scss.application.common.dto.SemesterDTO;
import com.capstone2024.scss.application.common.dto.SpecializationDTO;
import com.capstone2024.scss.infrastructure.repositories.DepartmentRepository;
import com.capstone2024.scss.infrastructure.repositories.MajorRepository;
import com.capstone2024.scss.infrastructure.repositories.SemesterRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicService {

    private final DepartmentRepository departmentRepository;
    private final MajorRepository majorRepository;
    private final SpecializationRepository specializationRepository;
    private final SemesterRepository semesterRepository;

    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(department -> DepartmentDTO.builder()
                        .id(department.getId())
                        .name(department.getName())
                        .code(department.getCode())
                        .build())
                .collect(Collectors.toList());
    }

    public List<MajorDTO> getMajorsByDepartmentId(Long departmentId) {
        return majorRepository.findByDepartmentId(departmentId).stream()
                .map(major -> MajorDTO.builder()
                        .id(major.getId())
                        .name(major.getName())
                        .code(major.getCode())
                        .build())
                .collect(Collectors.toList());
    }

    public List<SpecializationDTO> getSpecializationsByMajorId(Long majorId) {
        return specializationRepository.findByMajorId(majorId).stream()
                .map(specialization -> SpecializationDTO.builder()
                        .id(specialization.getId())
                        .name(specialization.getName())
                        .code(specialization.getCode())
                        .build())
                .collect(Collectors.toList());
    }

    public List<SemesterDTO> getAllSemesters() {
        return semesterRepository.findAll().stream()
                .map(semester -> new SemesterDTO(semester.getId(), semester.getName()))
                .collect(Collectors.toList());
    }
}


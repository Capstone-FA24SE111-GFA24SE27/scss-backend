package com.capstone2024.scss.application.academic.controller;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.ForbiddenException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.common.dto.DepartmentDTO;
import com.capstone2024.scss.application.common.dto.MajorDTO;
import com.capstone2024.scss.application.common.dto.SemesterDTO;
import com.capstone2024.scss.application.common.dto.SpecializationDTO;
import com.capstone2024.scss.domain.counselor.dto.*;
import com.capstone2024.scss.domain.counselor.entities.Specialization;
import com.capstone2024.scss.domain.student.entities.Department;
import com.capstone2024.scss.domain.student.entities.Major;
import com.capstone2024.scss.infrastructure.repositories.DepartmentRepository;
import com.capstone2024.scss.infrastructure.repositories.MajorRepository;
import com.capstone2024.scss.infrastructure.repositories.SemesterRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // Department Services
    public DepartmentDTO createDepartment(CreateDepartmentDTO dto) {
        Department department = Department.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .build();
        department = departmentRepository.save(department);
        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .build();
    }

    public DepartmentDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found"));
        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .build();
    }

    public DepartmentDTO updateDepartment(Long id, UpdateDepartmentDTO dto) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found"));
        department.setName(dto.getName());
        department.setCode(dto.getCode());
        department = departmentRepository.save(department);
        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .build();
    }

    // Major Services
    public MajorDTO createMajor(CreateMajorDTO dto) {
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Department not found"));
        Major major = Major.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .department(department)
                .build();
        major = majorRepository.save(major);
        return MajorDTO.builder()
                .id(major.getId())
                .name(major.getName())
                .code(major.getCode())
                .departmentId(department.getId())
                .build();
    }

    public MajorDTO getMajorById(Long id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Major not found"));
        return MajorDTO.builder()
                .id(major.getId())
                .name(major.getName())
                .code(major.getCode())
                .departmentId(major.getDepartment().getId())
                .build();
    }

    public MajorDTO updateMajor(Long id, UpdateMajorDTO dto) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Major not found"));
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Department not found"));
        major.setName(dto.getName());
        major.setCode(dto.getCode());
        major.setDepartment(department);
        major = majorRepository.save(major);
        return MajorDTO.builder()
                .id(major.getId())
                .name(major.getName())
                .code(major.getCode())
                .departmentId(department.getId())
                .build();
    }

    // Specialization Services
    public SpecializationDTO createSpecialization(CreateSpecializationDTO dto) {
        Major major = majorRepository.findById(dto.getMajorId())
                .orElseThrow(() -> new NotFoundException("Major not found"));
        Specialization specialization = Specialization.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .major(major)
                .build();
        specialization = specializationRepository.save(specialization);
        return SpecializationDTO.builder()
                .id(specialization.getId())
                .name(specialization.getName())
                .code(specialization.getCode())
                .majorId(major.getId())
                .build();
    }

    public SpecializationDTO getSpecializationById(Long id) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Specialization not found"));
        return SpecializationDTO.builder()
                .id(specialization.getId())
                .name(specialization.getName())
                .code(specialization.getCode())
                .majorId(specialization.getMajor().getId())
                .build();
    }

    public SpecializationDTO updateSpecialization(Long id, UpdateSpecializationDTO dto) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Specialization not found"));
        Major major = majorRepository.findById(dto.getMajorId())
                .orElseThrow(() -> new NotFoundException("Major not found"));
        specialization.setName(dto.getName());
        specialization.setCode(dto.getCode());
        specialization.setMajor(major);
        specialization = specializationRepository.save(specialization);
        return SpecializationDTO.builder()
                .id(specialization.getId())
                .name(specialization.getName())
                .code(specialization.getCode())
                .majorId(major.getId())
                .build();
    }

    // Delete Methods
    public void deleteDepartment(Long id) {
        try {
            departmentRepository.deleteById(id);
        } catch (Exception e) {
            throw new ForbiddenException("There is constrain remaining");
        }
    }

    public void deleteMajor(Long id) {
        try {
            majorRepository.deleteById(id);
        } catch (Exception e) {
            throw new ForbiddenException("There is constrain remaining");
        }
    }

    public void deleteSpecialization(Long id) {
        try {
            specializationRepository.deleteById(id);
        } catch (Exception e) {
            throw new ForbiddenException("There is constrain remaining");
        }
    }

    public Page<DepartmentDTO> filterDepartments(String keyword, String sortBy, SortDirection sortDirection, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.valueOf(sortDirection.name()), sortBy));
        return departmentRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(department -> DepartmentDTO.builder()
                        .id(department.getId())
                        .name(department.getName())
                        .code(department.getCode())
                        .build());
    }

    public Page<MajorDTO> filterMajors(String keyword, String sortBy, SortDirection sortDirection, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.valueOf(sortDirection.name()), sortBy));
        return majorRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(major -> MajorDTO.builder()
                        .id(major.getId())
                        .name(major.getName())
                        .code(major.getCode())
                        .departmentId(major.getDepartment().getId())
                        .build());
    }

    public Page<SpecializationDTO> filterSpecializations(String keyword, String sortBy, SortDirection sortDirection, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.valueOf(sortDirection.name()), sortBy));
        return specializationRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(specialization -> SpecializationDTO.builder()
                        .id(specialization.getId())
                        .name(specialization.getName())
                        .code(specialization.getCode())
                        .majorId(specialization.getMajor().getId())
                        .departmentId(specialization.getMajor().getDepartment().getId())
                        .build());
    }
}


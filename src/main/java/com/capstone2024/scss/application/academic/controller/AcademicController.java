package com.capstone2024.scss.application.academic.controller;

import com.capstone2024.scss.application.common.dto.DepartmentDTO;
import com.capstone2024.scss.application.common.dto.MajorDTO;
import com.capstone2024.scss.application.common.dto.SemesterDTO;
import com.capstone2024.scss.application.common.dto.SpecializationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/academic")
@Tag(name = "Academic", description = "APIs related to academic management")
@RequiredArgsConstructor
public class AcademicController {

    private static final Logger logger = LoggerFactory.getLogger(AcademicController.class);
    private final AcademicService academicService;

    // 1. Get all departments
    @Operation(summary = "Get all departments", description = "Retrieve a list of all departments")
    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        logger.info("Fetching all departments");
        List<DepartmentDTO> departments = academicService.getAllDepartments();
        logger.debug("Departments found: {}", departments.size());
        return ResponseEntity.ok(departments);
    }

    // 2. Get majors by department ID
    @Operation(summary = "Get majors by department ID", description = "Retrieve majors for a specific department ID")
    @GetMapping("/departments/{departmentId}/majors")
    public ResponseEntity<List<MajorDTO>> getMajorsByDepartmentId(@PathVariable Long departmentId) {
        logger.info("Fetching majors for department ID: {}", departmentId);
        List<MajorDTO> majors = academicService.getMajorsByDepartmentId(departmentId);
        logger.debug("Majors found: {}", majors.size());
        return ResponseEntity.ok(majors);
    }

    // 3. Get specializations by major ID
    @Operation(summary = "Get specializations by major ID", description = "Retrieve specializations for a specific major ID")
    @GetMapping("/majors/{majorId}/specializations")
    public ResponseEntity<List<SpecializationDTO>> getSpecializationsByMajorId(@PathVariable Long majorId) {
        logger.info("Fetching specializations for major ID: {}", majorId);
        List<SpecializationDTO> specializations = academicService.getSpecializationsByMajorId(majorId);
        logger.debug("Specializations found: {}", specializations.size());
        return ResponseEntity.ok(specializations);
    }

    @GetMapping("/semester")
    public List<SemesterDTO> getAllSemesters() {
        return academicService.getAllSemesters();
    }
}

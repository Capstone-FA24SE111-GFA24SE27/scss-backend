package com.capstone2024.scss.application.academic.controller;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.common.dto.*;
import com.capstone2024.scss.domain.counselor.dto.*;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/departments/filter")
    public ResponseEntity<Object> filterDepartments(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        Page<DepartmentDTO> result = academicService.filterDepartments(keyword, sortBy, sortDirection, page, size);
        return ResponseEntity.ok(PaginationDTO.<List<DepartmentDTO>>builder()
                .data(result.getContent())
                .totalPages(result.getTotalPages())
                .totalElements((int) result.getTotalElements())
                .build());
    }

    // Departments
    @PostMapping("/departments")
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody CreateDepartmentDTO dto) {
        return ResponseEntity.ok(academicService.createDepartment(dto));
    }

    @GetMapping("/departments/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(academicService.getDepartmentById(id));
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long id, @Valid @RequestBody UpdateDepartmentDTO dto) {
        return ResponseEntity.ok(academicService.updateDepartment(id, dto));
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        academicService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
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

    @GetMapping("/majors/filter")
    public ResponseEntity<Object> filterMajors(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        Page<MajorDTO> result = academicService.filterMajors(keyword, sortBy, sortDirection, page, size);
        return ResponseEntity.ok(PaginationDTO.<List<MajorDTO>>builder()
                .data(result.getContent())
                .totalPages(result.getTotalPages())
                .totalElements((int) result.getTotalElements())
                .build());
    }

    // Majors
    @PostMapping("/majors")
    public ResponseEntity<MajorDTO> createMajor(@Valid @RequestBody CreateMajorDTO dto) {
        return ResponseEntity.ok(academicService.createMajor(dto));
    }

    @GetMapping("/majors/{id}")
    public ResponseEntity<MajorDTO> getMajorById(@PathVariable Long id) {
        return ResponseEntity.ok(academicService.getMajorById(id));
    }

    @PutMapping("/majors/{id}")
    public ResponseEntity<MajorDTO> updateMajor(@PathVariable Long id, @Valid @RequestBody UpdateMajorDTO dto) {
        return ResponseEntity.ok(academicService.updateMajor(id, dto));
    }

    @DeleteMapping("/majors/{id}")
    public ResponseEntity<Void> deleteMajor(@PathVariable Long id) {
        academicService.deleteMajor(id);
        return ResponseEntity.noContent().build();
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

    @GetMapping("/specializations/filter")
    public ResponseEntity<Object> filterSpecializationsByMajorId(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        Page<SpecializationDTO> result = academicService.filterSpecializations(keyword, sortBy, sortDirection, page, size);
        return ResponseEntity.ok(PaginationDTO.<List<SpecializationDTO>>builder()
                .data(result.getContent())
                .totalPages(result.getTotalPages())
                .totalElements((int) result.getTotalElements())
                .build());
    }

    // Specializations
    @PostMapping("/specializations")
    public ResponseEntity<SpecializationDTO> createSpecialization(@Valid @RequestBody CreateSpecializationDTO dto) {
        return ResponseEntity.ok(academicService.createSpecialization(dto));
    }

    @GetMapping("/specializations/{id}")
    public ResponseEntity<SpecializationDTO> getSpecializationById(@PathVariable Long id) {
        return ResponseEntity.ok(academicService.getSpecializationById(id));
    }

    @PutMapping("/specializations/{id}")
    public ResponseEntity<SpecializationDTO> updateSpecialization(@PathVariable Long id, @Valid @RequestBody UpdateSpecializationDTO dto) {
        return ResponseEntity.ok(academicService.updateSpecialization(id, dto));
    }

    @DeleteMapping("/specializations/{id}")
    public ResponseEntity<Void> deleteSpecialization(@PathVariable Long id) {
        academicService.deleteSpecialization(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/semester")
    public List<SemesterDTO> getAllSemesters() {
        return academicService.getAllSemesters();
    }

    // DTO for Departments
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public class DepartmentDTO {
//        private Long id;
//
//        @NotBlank(message = "Name is required")
//        private String name;
//
//        @NotBlank(message = "Code is required")
//        private String code;
//    }

    // DTO for Majors
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public class MajorDTO {
//        private Long id;
//
//        @NotBlank(message = "Name is required")
//        private String name;
//
//        @NotBlank(message = "Code is required")
//        private String code;
//
//        @NotNull(message = "Department ID is required")
//        private Long departmentId;
//    }

    // DTO for Specializations
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public class SpecializationDTO {
//        private Long id;
//
//        @NotBlank(message = "Name is required")
//        private String name;
//
//        @NotBlank(message = "Code is required")
//        private String code;
//
//        @NotNull(message = "Major ID is required")
//        private Long majorId;
//
//        @NotNull(message = "Department ID is required")
//        private Long departmentId;
//    }

}

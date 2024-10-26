package com.capstone2024.scss.domain.common.mapper.account;

import com.capstone2024.scss.application.common.dto.DepartmentDTO;
import com.capstone2024.scss.application.common.dto.MajorDTO;
import com.capstone2024.scss.application.common.dto.SpecializationDTO;
import com.capstone2024.scss.domain.counselor.entities.Specialization;
import com.capstone2024.scss.domain.student.entities.Department;
import com.capstone2024.scss.domain.student.entities.Major;

public class AcademicDepartmentDetailMapper {
    public static SpecializationDTO toSpecializationDTO(Specialization specialization) {
        if (specialization == null) {
            return null;
        }

        return SpecializationDTO.builder()
                .id(specialization.getId())
                .name(specialization.getName())
                .majorId(specialization.getMajor() != null ? specialization.getMajor().getId() : null)
                .build();
    }

    public static DepartmentDTO toDepartmentDTO(Department department) {
        if (department == null) {
            return null;
        }

        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .build();
    }

    public static MajorDTO toMajorDTO(Major major) {
        if (major == null) {
            return null;
        }

        return MajorDTO.builder()
                .id(major.getId())
                .name(major.getName())
                .code(major.getCode())
                .departmentId(major.getDepartment() != null ? major.getDepartment().getId() : null) // Lấy ID của department
                .build();
    }
}

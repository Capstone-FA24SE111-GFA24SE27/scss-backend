package com.capstone2024.scss.domain.common.mapper.appointment_counseling;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.domain.common.mapper.account.ProfileMapper;
import com.capstone2024.scss.domain.student.entities.Student;

public class StudentProfileMapper {
    public static StudentProfileDTO toStudentProfileDTO(Student student) {
        if (student == null) {
            return null;
        }

        return StudentProfileDTO.builder()
                .id(student.getId())
                .profile(ProfileMapper.toProfileDTO(student))
                .studentCode(student.getStudentCode())
                .email(student.getAccount().getEmail())
                .build();
    }
}

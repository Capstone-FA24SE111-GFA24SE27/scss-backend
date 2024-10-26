package com.capstone2024.scss.domain.student.services;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.student.dto.StudentCounselingProfileRequestDTO;
import com.capstone2024.scss.application.student.dto.StudentDocumentDTO;
import com.capstone2024.scss.application.student.dto.StudentFilterRequestDTO;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    StudentProfileDTO getStudentById(Long id);

    StudentDocumentDTO getStudentDocumentById(Long id);

    PaginationDTO<List<StudentProfileDTO>> getStudents(StudentFilterRequestDTO filterRequest);

    StudentProfileDTO getStudentByStudentCode(String studentCode);

    void createCounselingProfile(StudentCounselingProfileRequestDTO requestDTO, Long studentId);

    void updateCounselingProfile(StudentCounselingProfileRequestDTO requestDTO, Long studentId);
}

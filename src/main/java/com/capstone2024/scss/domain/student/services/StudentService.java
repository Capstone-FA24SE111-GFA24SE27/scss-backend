package com.capstone2024.scss.domain.student.services;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.student.dto.StudentDocumentDTO;

import java.util.Optional;

public interface StudentService {
    StudentProfileDTO getStudentById(Long id);

    StudentDocumentDTO getStudentDocumentById(Long id);
}

package com.capstone2024.scss.domain.student.services.impl;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.StudentProfileMapper;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.services.StudentService;
import com.capstone2024.scss.infrastructure.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public StudentProfileDTO getStudentById(Long id) {
        Student student = studentRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Student with ID:" + id + " not found"));
        return StudentProfileMapper.toStudentProfileDTO(student);
    }
}

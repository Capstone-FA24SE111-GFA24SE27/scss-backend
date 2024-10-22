package com.capstone2024.scss.domain.student.services.impl;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.student.dto.StudentDocumentDTO;
import com.capstone2024.scss.application.student.dto.StudentFilterRequestDTO;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.services.StudentService;
import com.capstone2024.scss.domain.common.mapper.student.StudentMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.infrastructure.repositories.StudentRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingAppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CounselingAppointmentRepository appointmentRepository;

    @Override
    public StudentProfileDTO getStudentById(Long id) {
        Student student = studentRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Student with ID:" + id + " not found"));
        return StudentMapper.toStudentProfileDTO(student);
    }

    @Override
    public StudentProfileDTO getStudentByStudentCode(String studentCode) {
        Student student = studentRepository
                .findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Student with code: " + studentCode + " not found"));

        return StudentMapper.toStudentProfileDTO(student);
    }

    @Override
    public StudentDocumentDTO getStudentDocumentById(Long studentId) {
        Student student = studentRepository
                .findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student with ID:" + studentId + " not found"));

        List<CounselingAppointment> appointments = appointmentRepository.findAllByStudentId(studentId);

        return StudentMapper.toStudentDocumentDTO(student, appointments);
    }

    @Override
    public PaginationDTO<List<StudentProfileDTO>> getStudents(StudentFilterRequestDTO filterRequest) {
        Page<Student> studentPage = studentRepository.findStudents(
                filterRequest.getStudentCode(),
                filterRequest.getSpecializationId(),
                filterRequest.getKeyword(),
                filterRequest.getPagination());

        List<StudentProfileDTO> studentDTOs = studentPage.getContent().stream()
                .map(StudentMapper::toStudentProfileDTO)
                .collect(Collectors.toList());

        return PaginationDTO.<List<StudentProfileDTO>>builder()
                .data(studentDTOs)
                .totalPages(studentPage.getTotalPages())
                .totalElements((int) studentPage.getTotalElements())
                .build();
    }
}

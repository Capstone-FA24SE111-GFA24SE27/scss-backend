package com.capstone2024.scss.domain.common.mapper.student;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.student.dto.StudentDocumentDTO;
import com.capstone2024.scss.domain.common.mapper.account.ProfileMapper;
import com.capstone2024.scss.domain.common.mapper.account.SpecializationMapper;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.CounselingAppointmentMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.student.entities.Student;

import java.util.List;

public class StudentMapper {
    public static StudentProfileDTO toStudentProfileDTO(Student student) {
        if (student == null) {
            return null;
        }

        return StudentProfileDTO.builder()
                .id(student.getId())
                .profile(ProfileMapper.toProfileDTO(student))
                .studentCode(student.getStudentCode())
                .email(student.getAccount().getEmail())
                .specialization(SpecializationMapper.toSpecializationDTO(student.getSpecialization()))
                .build();
    }

    public static StudentDocumentDTO toStudentDocumentDTO(Student student, List<CounselingAppointment> appointments) {
        if (student == null) {
            return null;
        }

        StudentDocumentDTO studentDocumentDTO = new StudentDocumentDTO();

        StudentProfileDTO studentProfileDTO = StudentProfileDTO.builder()
                .id(student.getId())
                .profile(ProfileMapper.toProfileDTO(student))
                .studentCode(student.getStudentCode())
                .email(student.getAccount().getEmail())
                .specialization(SpecializationMapper.toSpecializationDTO(student.getSpecialization()))
                .build();

        studentDocumentDTO.setStudentProfile(studentProfileDTO);

        if(appointments != null) {
            List<CounselingAppointmentDTO> appointmentsDTO = appointments.stream()
                    .map(CounselingAppointmentMapper::toCounselingAppointmentDTO)
                    .toList();

            studentDocumentDTO.setCounselingAppointment(appointmentsDTO);
        }

        return studentDocumentDTO;
    }
}

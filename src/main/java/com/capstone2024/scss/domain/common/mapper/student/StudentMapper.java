package com.capstone2024.scss.domain.common.mapper.student;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.student.dto.StudentCounselingProfileDTO;
import com.capstone2024.scss.application.student.dto.StudentDocumentDTO;
import com.capstone2024.scss.domain.common.mapper.account.ProfileMapper;
import com.capstone2024.scss.domain.common.mapper.account.AcademicDepartmentDetailMapper;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.CounselingAppointmentMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.entities.StudentCounselingProfile;

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
                .specialization(AcademicDepartmentDetailMapper.toSpecializationDTO(student.getSpecialization()))
                .department(AcademicDepartmentDetailMapper.toDepartmentDTO(student.getDepartment()))
                .major(AcademicDepartmentDetailMapper.toMajorDTO(student.getMajor()))
                .build();
    }

    public static StudentDocumentDTO toStudentDocumentDTO(Student student, List<CounselingAppointment> appointments) {
        if (student == null) {
            return null;
        }

        StudentDocumentDTO studentDocumentDTO = new StudentDocumentDTO();

        studentDocumentDTO.setStudentProfile(toStudentProfileDTO(student));

        if(appointments != null) {
            List<CounselingAppointmentDTO> appointmentsDTO = appointments.stream()
                    .map(CounselingAppointmentMapper::toCounselingAppointmentDTO)
                    .toList();

            studentDocumentDTO.setCounselingAppointment(appointmentsDTO);
        }

        studentDocumentDTO.setCounselingProfile(toStudentCounselingProfileDTO(student.getCounselingProfile()));

        return studentDocumentDTO;
    }

    public static StudentCounselingProfileDTO toStudentCounselingProfileDTO(StudentCounselingProfile profile) {
        if (profile == null) {
            return null;
        }

        StudentCounselingProfileDTO dto = new StudentCounselingProfileDTO();
        dto.setIntroduction(profile.getIntroduction());
        dto.setCurrentHealthStatus(profile.getCurrentHealthStatus());
        dto.setPsychologicalStatus(profile.getPsychologicalStatus());
        dto.setStressFactors(profile.getStressFactors());
        dto.setAcademicDifficulties(profile.getAcademicDifficulties());
        dto.setStudyPlan(profile.getStudyPlan());
        dto.setCareerGoals(profile.getCareerGoals());
        dto.setPartTimeExperience(profile.getPartTimeExperience());
        dto.setInternshipProgram(profile.getInternshipProgram());
        dto.setExtracurricularActivities(profile.getExtracurricularActivities());
        dto.setPersonalInterests(profile.getPersonalInterests());
        dto.setSocialRelationships(profile.getSocialRelationships());
        dto.setFinancialSituation(profile.getFinancialSituation());
        dto.setFinancialSupport(profile.getFinancialSupport());
//        dto.setCounselingIssue(profile.getCounselingIssue());
//        dto.setCounselingGoal(profile.getCounselingGoal());
        dto.setDesiredCounselingFields(profile.getDesiredCounselingFields());
        dto.setStatus(profile.getStatus());

        return dto;
    }
}

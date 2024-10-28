package com.capstone2024.scss.domain.student.services.impl;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.student.dto.StudentCounselingProfileRequestDTO;
import com.capstone2024.scss.application.student.dto.StudentDocumentDTO;
import com.capstone2024.scss.application.student.dto.StudentFilterRequestDTO;
import com.capstone2024.scss.application.student.dto.StudyDTO;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.entities.StudentCounselingProfile;
import com.capstone2024.scss.domain.student.enums.CounselingProfileStatus;
import com.capstone2024.scss.domain.student.services.StudentService;
import com.capstone2024.scss.domain.common.mapper.student.StudentMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.infrastructure.repositories.student.CounselingProfileRepository;
import com.capstone2024.scss.infrastructure.repositories.student.StudentRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingAppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CounselingAppointmentRepository appointmentRepository;
    private final CounselingProfileRepository counselingProfileRepository;
    private final RestTemplate restTemplate;

    @Value("${server.api.fap.system.base.url}")
    private String fapServerUrl;

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
    public void createCounselingProfile(StudentCounselingProfileRequestDTO requestDTO, Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + studentId));

        // Tạo đối tượng StudentCounselingProfile mới
        StudentCounselingProfile profile = counselingProfileRepository.findByStudent(student)
                .orElse(new StudentCounselingProfile());
        profile.setIntroduction(requestDTO.getIntroduction());
        profile.setCurrentHealthStatus(requestDTO.getCurrentHealthStatus());
        profile.setPsychologicalStatus(requestDTO.getPsychologicalStatus());
        profile.setStressFactors(requestDTO.getStressFactors());
        profile.setAcademicDifficulties(requestDTO.getAcademicDifficulties());
        profile.setStudyPlan(requestDTO.getStudyPlan());
        profile.setCareerGoals(requestDTO.getCareerGoals());
        profile.setPartTimeExperience(requestDTO.getPartTimeExperience());
        profile.setInternshipProgram(requestDTO.getInternshipProgram());
        profile.setExtracurricularActivities(requestDTO.getExtracurricularActivities());
        profile.setPersonalInterests(requestDTO.getPersonalInterests());
        profile.setSocialRelationships(requestDTO.getSocialRelationships());
        profile.setFinancialSituation(requestDTO.getFinancialSituation());
        profile.setFinancialSupport(requestDTO.getFinancialSupport());
//        profile.setCounselingIssue(requestDTO.getCounselingIssue());
//        profile.setCounselingGoal(requestDTO.getCounselingGoal());
        profile.setDesiredCounselingFields(requestDTO.getDesiredCounselingFields());
        profile.setStatus(CounselingProfileStatus.UNVERIFIED); // Trạng thái mặc định

        // Thiết lập Student cho CounselingProfile
        student.setCounselingProfile(profile);

        // Lưu CounselingProfile vào cơ sở dữ liệu
        studentRepository.save(student);
    }

    @Override
    public void updateCounselingProfile(StudentCounselingProfileRequestDTO requestDTO, Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + studentId));

        // Tìm hồ sơ tư vấn hiện tại
        StudentCounselingProfile profile = counselingProfileRepository.findByStudent(student)
                .orElseThrow(() -> new NotFoundException("Counseling profile not found for student with id " + studentId));

        // Cập nhật thông tin cho hồ sơ tư vấn
        profile.setIntroduction(requestDTO.getIntroduction());
        profile.setCurrentHealthStatus(requestDTO.getCurrentHealthStatus());
        profile.setPsychologicalStatus(requestDTO.getPsychologicalStatus());
        profile.setStressFactors(requestDTO.getStressFactors());
        profile.setAcademicDifficulties(requestDTO.getAcademicDifficulties());
        profile.setStudyPlan(requestDTO.getStudyPlan());
        profile.setCareerGoals(requestDTO.getCareerGoals());
        profile.setPartTimeExperience(requestDTO.getPartTimeExperience());
        profile.setInternshipProgram(requestDTO.getInternshipProgram());
        profile.setExtracurricularActivities(requestDTO.getExtracurricularActivities());
        profile.setPersonalInterests(requestDTO.getPersonalInterests());
        profile.setSocialRelationships(requestDTO.getSocialRelationships());
        profile.setFinancialSituation(requestDTO.getFinancialSituation());
        profile.setFinancialSupport(requestDTO.getFinancialSupport());
//        profile.setCounselingIssue(requestDTO.getCounselingIssue());
//        profile.setCounselingGoal(requestDTO.getCounselingGoal());
        profile.setDesiredCounselingFields(requestDTO.getDesiredCounselingFields());

        // Lưu hồ sơ tư vấn đã cập nhật vào cơ sở dữ liệu
        counselingProfileRepository.save(profile);
    }

    @Override
    public List<StudyDTO> getStudiesByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + studentId));

        String url = fapServerUrl + "/api/studies/" + student.getStudentCode();
        StudyDTO[] studiesArray = restTemplate.getForObject(url, StudyDTO[].class);
        return List.of(studiesArray);
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

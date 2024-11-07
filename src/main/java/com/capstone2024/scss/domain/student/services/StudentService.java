package com.capstone2024.scss.domain.student.services;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.student.dto.*;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    StudentProfileDTO getStudentById(Long id);

    StudentDocumentDTO getStudentDocumentById(Long id);

    PaginationDTO<List<StudentDetailForFilterDTO>> getStudents(StudentFilterRequestDTO filterRequest);

    StudentProfileDTO getStudentByStudentCode(String studentCode);

    void createCounselingProfile(StudentCounselingProfileRequestDTO requestDTO, Long studentId);

    void updateCounselingProfile(StudentCounselingProfileRequestDTO requestDTO, Long studentId);

    List<StudyDTO> getStudiesByStudentId(Long studentId);

    List<AttendanceDTO> getAttendanceByStudentCodeAndSemesterName(Long studentId, String semesterName);

    List<AttendanceDetailDTO> getAttendanceDetailsByStudentCodeAndAttendanceId(Long studentId, Long attendanceId);

    Object getDemandProblemTagDetailByStudentAndSemester(Long studentId, String semesterName);

    PaginationDTO<List<StudentDetailForFilterDTO>> getStudentsWithRecommend(StudentFilterRequestDTO filterRequest);

    void excludeAllDemandProblemTagsByStudentId(Long studentId);
}

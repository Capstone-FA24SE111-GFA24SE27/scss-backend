package com.capstone2024.scss.domain.common.mapper.student.academic;

import com.capstone2024.scss.application.student.dto.AttendanceDTO;
import com.capstone2024.scss.application.student.dto.AttendanceDetailDTO;
import com.capstone2024.scss.application.student.dto.StudyDTO;
import com.capstone2024.scss.domain.common.entity.Semester;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.entities.academic.AcademicTranscript;
import com.capstone2024.scss.domain.student.entities.academic.AttendanceDetail;
import com.capstone2024.scss.domain.student.entities.academic.StudentStudy;
import com.capstone2024.scss.domain.student.entities.academic.enums.AttendanceStatus;

import java.util.List;
import java.util.stream.Collectors;

public class StudentStudyMapper {
    public static StudentStudy toStudentStudy(AttendanceDTO attendanceDTO, Student student, Semester semester) {
        if (attendanceDTO == null) {
            return null;
        }

        // Map AttendanceDTO to StudentStudy
        StudentStudy studentStudy = StudentStudy.builder()
                .startDate(attendanceDTO.getStartDate())
                .totalSlot(attendanceDTO.getTotalSlot())
                .finalGrade(attendanceDTO.getGrade())  // Assuming finalGrade is grade in AttendanceDTO
                .subjectName(attendanceDTO.getSubjectName())
                .subjectCode(attendanceDTO.getSubjectCode())  // Assuming subjectCode is studentCode in AttendanceDTO
                .student(student)  // This should be set as a Student object, passed as parameter
                .semester(semester)  // This should be set as a Semester object, passed as parameter
                .status(StudentStudy.StudyStatus.valueOf(attendanceDTO.getStatus().name()))
                .build();

        studentStudy.setAttendanceDetails(mapAttendanceDetails(attendanceDTO.getDetais(), studentStudy));

        return studentStudy;
    }

    public static AcademicTranscript toAcademicTranscript(StudyDTO studyDTO, Student student, Semester semester) {
        if (studyDTO == null) {
            return null;
        }

        // Map AttendanceDTO to StudentStudy

        return AcademicTranscript.builder()
                .grade(studyDTO.getGrade())  // Assuming finalGrade is grade in AttendanceDTO
                .subjectName(studyDTO.getSubjectName())
                .subjectCode(studyDTO.getSubjectCode())  // Assuming subjectCode is studentCode in AttendanceDTO
                .student(student)  // This should be set as a Student object, passed as parameter
                .semester(semester)  // This should be set as a Semester object, passed as parameter
                .status(com.capstone2024.scss.domain.student.entities.academic.enums.StudyStatus.valueOf(studyDTO.getStatus()))
                .term(studyDTO.getTerm())
                .build();
    }

    private static List<AttendanceDetail> mapAttendanceDetails(List<AttendanceDetailDTO> attendanceDetailDTOs, StudentStudy studentStudy) {
        if (attendanceDetailDTOs == null) {
            return null;
        }
        return attendanceDetailDTOs.stream()
                .map(attendanceDetailDTO -> mapAttendanceDetail(attendanceDetailDTO, studentStudy))
                .collect(Collectors.toList());
    }

    public static AttendanceDetail mapAttendanceDetail(AttendanceDetailDTO attendanceDetailDTO, StudentStudy studentStudy) {
        if (attendanceDetailDTO == null) {
            return null;
        }

        return AttendanceDetail.builder()
                        .date(attendanceDetailDTO.getDate())
                        .slot(attendanceDetailDTO.getSlot())
                        .room(attendanceDetailDTO.getRoom())
                        .lecturer(attendanceDetailDTO.getLecturer())
                        .groupName(attendanceDetailDTO.getGroupName())
                        .status(AttendanceStatus.valueOf(attendanceDetailDTO.getStatus().name()))
                        .lecturerComment(attendanceDetailDTO.getLecturerComment())
                        .studentStudy(studentStudy)
                        .build();
    }
}

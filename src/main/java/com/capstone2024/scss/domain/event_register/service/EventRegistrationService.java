package com.capstone2024.scss.domain.event_register.service;

import com.capstone2024.scss.application.event_register.dto.StudentEventScheduleDTO;
import com.capstone2024.scss.domain.student.entities.Student;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRegistrationService {
    void registerStudentForEvent(Student student, Long eventScheduleId, boolean force);
    List<StudentEventScheduleDTO> getAllStudentSchedules(Long studentId, LocalDate fromDate, LocalDate toDate);
}

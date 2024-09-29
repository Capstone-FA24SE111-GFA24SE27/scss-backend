package com.capstone2024.scss.domain.event_register.service.impl;

import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.event_register.dto.StudentEventScheduleDTO;
import com.capstone2024.scss.domain.common.mapper.event.StudentEventScheduleMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.event.entities.EventSchedule;
import com.capstone2024.scss.domain.event.entities.enums.AttendanceStatus;
import com.capstone2024.scss.domain.event.entities.enums.RegisterStatus;
import com.capstone2024.scss.domain.event_register.entity.EventRegisterRequest;
import com.capstone2024.scss.domain.event_register.entity.StudentEventSchedule;
import com.capstone2024.scss.domain.event_register.service.EventRegistrationService;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.infrastructure.repositories.CounselingAppointmentRepository;
import com.capstone2024.scss.infrastructure.repositories.event.EventRegisterRequestRepository;
import com.capstone2024.scss.infrastructure.repositories.event.EventScheduleRepository;
import com.capstone2024.scss.infrastructure.repositories.event.StudentEventScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventRegistrationServiceImpl implements EventRegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(EventRegistrationService.class);

    private final EventScheduleRepository eventScheduleRepository;
    private final StudentEventScheduleRepository studentEventScheduleRepository;
    private final EventRegisterRequestRepository eventRegisterRequestRepository;
    private final CounselingAppointmentRepository counselingAppointmentRepository;

    public void registerStudentForEvent(Student student, Long eventScheduleId, boolean force) {
        // Fetch event schedule and student from the database
        EventSchedule eventSchedule = eventScheduleRepository.findById(eventScheduleId)
                .orElseThrow(() -> new NotFoundException("Event Schedule not found"));

        // Check for conflicts unless forced
        if (!force && hasConflicts(eventSchedule, student.getId())) {
            logger.warn("Registration failed for student {} due to overlapping schedules", student.getId());
            throw new BadRequestException("Event schedule overlaps with existing appointments or events.");
        }

        // Create register request
        EventRegisterRequest registerRequest = new EventRegisterRequest();
        registerRequest.setStudent(student);
        registerRequest.setEventSchedule(eventSchedule);
        registerRequest.setStatus(RegisterStatus.WAITING);

        // Check if the event requires acceptance
        if (eventSchedule.getEvent().getIsNeedAccept() == null || !eventSchedule.getEvent().getIsNeedAccept()) {
            // Automatically approve
            registerRequest.setStatus(RegisterStatus.APPROVED);
            createStudentEventSchedule(student, eventSchedule);
            logger.info("Student {} registered and approved for event schedule {}", student.getId(), eventScheduleId);
        } else {
            logger.info("Student {} registration request created for event schedule {}", student.getId(), eventScheduleId);
        }

        eventRegisterRequestRepository.save(registerRequest);
    }

    private boolean hasConflicts(EventSchedule eventSchedule, Long studentId) {
        LocalDateTime startDateTime = eventSchedule.getStartDate();
        LocalDateTime endDateTime = eventSchedule.getEndDate();

        // Check for overlapping counseling appointments for the student
        List<CounselingAppointment> conflictingAppointments = counselingAppointmentRepository
                .findAllByStudentIdAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(studentId, endDateTime, startDateTime);

        if (!conflictingAppointments.isEmpty()) {
            return true; // Overlap found with counseling appointments
        }

        // Check for overlapping event schedules for the student
        List<EventSchedule> conflictingSchedules = eventScheduleRepository
                .findAllByStudentIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(studentId, endDateTime, startDateTime);

        return !conflictingSchedules.isEmpty(); // Return true if overlaps are found
    }

    private void createStudentEventSchedule(Student student, EventSchedule eventSchedule) {
        StudentEventSchedule studentEventSchedule = new StudentEventSchedule();
        studentEventSchedule.setStudent(student);
        studentEventSchedule.setEventSchedule(eventSchedule);
        studentEventSchedule.setAttendanceStatus(AttendanceStatus.WAITING);
        studentEventScheduleRepository.save(studentEventSchedule);
    }

    public List<StudentEventScheduleDTO> getAllStudentSchedules(Long studentId, LocalDate fromDate, LocalDate toDate) {
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(LocalTime.MAX);
        logger.info("Fetching all student event schedules for studentId: {} from: {} to: {}", studentId, from, to);
        List<StudentEventSchedule> schedules = studentEventScheduleRepository.findAllByStudentIdAndDateRange(studentId, from, to);
        return schedules.stream()
                .map(StudentEventScheduleMapper::toDTO)
                .collect(Collectors.toList());
    }
}

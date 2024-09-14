package com.capstone2024.gym_management_system.domain.counseling_booking.services;

import com.capstone2024.gym_management_system.application.booking_counseling.dto.SlotDTO;
import com.capstone2024.gym_management_system.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.gym_management_system.domain.counseling_booking.entities.student.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CounselingAppointmentRequestService {
    Map<LocalDate, List<SlotDTO>> getDailySlots(Long counselorId, LocalDate from, LocalDate to, Long studentId);

    CounselingAppointmentRequest createAppointmentRequest(String slotCode, LocalDate date, Long counselorId, boolean isOnline, String reason, Student student);
}

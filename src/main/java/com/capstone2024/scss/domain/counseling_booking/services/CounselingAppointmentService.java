package com.capstone2024.scss.domain.counseling_booking.services;

import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.counceling_appointment.OfflineAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.counceling_appointment.OnlineAppointmentRequestDTO;

import java.time.LocalDate;
import java.util.List;

public interface CounselingAppointmentService {
    void approveOfflineAppointment(Long requestId, Long counselorId, OfflineAppointmentRequestDTO dto);

    void approveOnlineAppointment(Long requestId, Long counselorId, OnlineAppointmentRequestDTO dto);

    void denyAppointmentRequest(Long requestId, Long counselorId);

    List<CounselingAppointmentDTO> getAppointmentsForCounselor(LocalDate fromDate, LocalDate toDate, Long counselorId);

    List<CounselingAppointmentDTO> getAppointmentsForStudent(LocalDate fromDate, LocalDate toDate, Long studentId);
}

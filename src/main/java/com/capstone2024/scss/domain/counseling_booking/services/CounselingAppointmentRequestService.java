package com.capstone2024.scss.domain.counseling_booking.services;

import com.capstone2024.scss.application.booking_counseling.dto.SlotDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselingAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.AppointmentRequestFilterDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.UpdateAppointmentRequestDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.student.entities.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CounselingAppointmentRequestService {
    Map<LocalDate, List<SlotDTO>> getDailySlots(Long counselorId, LocalDate from, LocalDate to, Long studentId);

    CounselingAppointmentRequest createAppointmentRequest(String slotCode, LocalDate date, Long counselorId, boolean isOnline, String reason, Student student);

    PaginationDTO<List<CounselingAppointmentRequestDTO>> getAppointmentsRequest(Account principle, AppointmentRequestFilterDTO filterRequest);

    void updateAppointmentDetails(Long appointmentId, UpdateAppointmentRequestDTO updateRequest, Long counselorId);
}

package com.capstone2024.scss.domain.counseling_booking.services;

import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.counceling_appointment.AppointmentFeedbackDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.counceling_appointment.OfflineAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.counceling_appointment.OnlineAppointmentRequestDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.counseling_appointment.dto.AppointmentFilterDTO;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.student.entities.Student;

import java.time.LocalDate;
import java.util.List;

public interface CounselingAppointmentService {
    void approveOfflineAppointment(Long requestId, Long counselorId, OfflineAppointmentRequestDTO dto);

    void approveOnlineAppointment(Long requestId, Long counselorId, OnlineAppointmentRequestDTO dto);

    void denyAppointmentRequest(Long requestId, Long counselorId);

    List<CounselingAppointmentDTO> getAppointmentsForCounselor(LocalDate fromDate, LocalDate toDate, Long counselorId);

    List<CounselingAppointmentDTO> getAppointmentsForStudent(LocalDate fromDate, LocalDate toDate, Long studentId);

    void submitFeedback(Long appointmentId, AppointmentFeedbackDTO feedbackDTO, Long studentId);

    void takeAttendanceForAppointment(Long appointmentId, CounselingAppointmentStatus counselingAppointmentStatus, Long counselorId);

    PaginationDTO<List<CounselingAppointmentDTO>> getAppointmentsWithFilterForCounselor(AppointmentFilterDTO filterDTO, Counselor counselor);

    PaginationDTO<List<CounselingAppointmentDTO>> getAppointmentsWithFilterForStudent(AppointmentFilterDTO filterDTO, Student student);
}

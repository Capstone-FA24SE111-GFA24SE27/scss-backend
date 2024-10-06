package com.capstone2024.scss.domain.counselor.services;

import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselingAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.AppointmentRequestFilterDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.counseling_appointment.dto.AppointmentReportResponse;
import com.capstone2024.scss.application.counseling_appointment.dto.request.counseling_appointment.AppointmentFilterDTO;

import java.time.LocalDate;
import java.util.List;

public interface ManageCounselorService {
    PaginationDTO<List<CounselingAppointmentRequestDTO>> getAppointmentsRequest (Long counselorId, AppointmentRequestFilterDTO filterRequest);
    List<CounselingAppointmentDTO> getAppointmentsForCounselor(LocalDate fromDate, LocalDate toDate, Long counselorId);
    PaginationDTO<List<CounselingAppointmentDTO>> getAppointmentsWithFilterForCounselor(AppointmentFilterDTO filterDTO, Long counselorId);
    AppointmentReportResponse getAppointmentReportByAppointmentId(Long appointmentId, Long counselorId);
}

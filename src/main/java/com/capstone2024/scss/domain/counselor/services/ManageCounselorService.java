package com.capstone2024.scss.domain.counselor.services;

import com.capstone2024.scss.application.booking_counseling.dto.AppointmentFeedbackDTO;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselingAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.AppointmentRequestFilterDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.counseling_appointment.dto.AppointmentReportResponse;
import com.capstone2024.scss.application.counseling_appointment.dto.request.counseling_appointment.AppointmentFilterDTO;
import com.capstone2024.scss.application.counselor.dto.CounselingSlotDTO;
import com.capstone2024.scss.application.counselor.dto.ManageCounselorDTO;
import com.capstone2024.scss.application.counselor.dto.request.*;
import com.capstone2024.scss.application.q_and_a.dto.QuestionCardFeedbackDTO;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counselor.entities.AvailableDateRange;
import com.capstone2024.scss.domain.counselor.entities.SlotOfCounselor;
import com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public interface ManageCounselorService {
    PaginationDTO<List<CounselingAppointmentRequestDTO>> getAppointmentsRequestOfCounselorForManage(Long counselorId, AppointmentRequestFilterDTO filterRequest);
    List<CounselingAppointmentDTO> getAppointmentsForCounselor(LocalDate fromDate, LocalDate toDate, Long counselorId);
    PaginationDTO<List<CounselingAppointmentDTO>> getAppointmentsWithFilterForCounselor(AppointmentFilterDTO filterDTO, Long counselorId);
    AppointmentReportResponse getAppointmentReportByAppointmentId(Long appointmentId, Long counselorId);

    void updateCounselorStatus(Long counselorId, CounselorStatus status);

    void updateAvailableDateRange(Long counselorId, LocalDate startDate, LocalDate endDate);

    AvailableDateRange getAvailableDateRangeByCounselorId(Long counselorId);

    List<CounselingSlot> getAllCounselingSlots();

    List<SlotOfCounselor> getCounselingSlotsByCounselorId(Long counselorId);

    void assignSlotToCounselor(Long counselorId, Long slotId, DayOfWeek dayOfWeek);

    void unassignSlotFromCounselor(Long counselorId, Long slotId);

    PaginationDTO<List<AppointmentFeedbackDTO>> getFeedbackWithFilterForCounselor(FeedbackFilterDTO filterDTO, Long counselorId);

    PaginationDTO<List<ManageCounselorDTO>> getCounselorsWithFilter(CounselorFilterRequestDTO filterRequest);

    ManageCounselorDTO getOneCounselor(Long counselorId);

    PaginationDTO<List<ManageCounselorDTO>> getAcademicCounselorsWithFilter(AcademicCounselorFilterRequestDTO filterRequest);

    PaginationDTO<List<ManageCounselorDTO>> getNonAcademicCounselorsWithFilter(NonAcademicCounselorFilterRequestDTO filterRequest);

    CounselingSlotDTO createCounselingSlot(CreateCounselingSlotRequestDTO createCounselingSlotDTO);

    PaginationDTO<List<QuestionCardFeedbackDTO>> getQCFeedbackWithFilterForCounselor(FeedbackFilterDTO filterDTO, Long counselorId);
}

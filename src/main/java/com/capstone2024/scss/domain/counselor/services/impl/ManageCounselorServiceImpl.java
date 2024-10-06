package com.capstone2024.scss.domain.counselor.services.impl;

import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselingAppointmentRequestDTO;
import com.capstone2024.scss.application.booking_counseling.dto.request.AppointmentRequestFilterDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.counseling_appointment.dto.AppointmentReportResponse;
import com.capstone2024.scss.application.counseling_appointment.dto.request.counseling_appointment.AppointmentFilterDTO;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentRequestService;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentService;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.counselor.services.CounselorService;
import com.capstone2024.scss.domain.counselor.services.ManageCounselorService;
import com.capstone2024.scss.infrastructure.repositories.counselor.CounselorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManageCounselorServiceImpl implements ManageCounselorService {
    private final CounselorService counselorService;
    private final CounselingAppointmentRequestService counselingAppointmentRequestService;
    private final CounselorRepository counselorRepository;
    private final CounselingAppointmentService appointmentService;

    public PaginationDTO<List<CounselingAppointmentRequestDTO>> getAppointmentsRequest (Long counselorId, AppointmentRequestFilterDTO filterDTO) {
        Counselor counselor = checkForCounselor(counselorId);

        return counselingAppointmentRequestService.getAppointmentsRequest(counselor.getAccount(), filterDTO);
    }

    public List<CounselingAppointmentDTO> getAppointmentsForCounselor(LocalDate fromDate, LocalDate toDate, Long counselorId) {
        checkForCounselor(counselorId);

        return appointmentService.getAppointmentsForCounselor(fromDate, toDate, counselorId);
    }

    private Counselor checkForCounselor(Long counselorId) {
        return counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found with id: " + counselorId));
    }

    public PaginationDTO<List<CounselingAppointmentDTO>> getAppointmentsWithFilterForCounselor(AppointmentFilterDTO filterDTO, Long counselorId) {
        Counselor counselor = checkForCounselor(counselorId);

        return appointmentService.getAppointmentsWithFilterForCounselor(filterDTO, counselor);
    }

    public AppointmentReportResponse getAppointmentReportByAppointmentId(Long appointmentId, Long counselorId) {
        Counselor counselor = checkForCounselor(counselorId);

        return appointmentService.getAppointmentReportByAppointmentId(appointmentId, counselor);
    }
}

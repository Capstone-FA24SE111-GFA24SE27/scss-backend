package com.capstone2024.scss.application.counseling_appointment.dto.request.counseling_appointment;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AppointmentFilterDTO {
    private String studentCode;
    private LocalDate fromDate;
    private LocalDate toDate;
    private CounselingAppointmentStatus status;
    private String sortBy;
    private SortDirection sortDirection;
    private int page;
}

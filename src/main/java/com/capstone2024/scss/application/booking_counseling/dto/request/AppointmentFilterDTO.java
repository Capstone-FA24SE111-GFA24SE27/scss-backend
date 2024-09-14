package com.capstone2024.scss.application.booking_counseling.dto.request;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

@Data
@Builder
public class AppointmentFilterDTO {

    private String search;
    private MeetingType meetingType;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String sortBy;
    private SortDirection sortDirection;
    private Pageable pagination;
}

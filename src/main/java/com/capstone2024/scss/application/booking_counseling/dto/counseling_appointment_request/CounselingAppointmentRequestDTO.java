package com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request;

import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class CounselingAppointmentRequestDTO {
    private Long id;
    private LocalDate requireDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private MeetingType meetingType;
    private CounselingAppointmentRequest.RequestType requestType;
    private String reason;
    private AppointmentDetailsDTO appointmentDetails;

    private CounselorProfileDTO counselor;
    private StudentProfileDTO student;
}

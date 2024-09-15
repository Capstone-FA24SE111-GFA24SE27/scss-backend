package com.capstone2024.scss.application.booking_counseling.dto;

import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.CounselorAppointmentDTO;
import com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request.StudentAppointmentDTO;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CounselingAppointmentDTO {
    private Long id;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private CounselingAppointmentStatus status;
    private MeetingType meetingType;
    private String meetUrl;  // Dành cho OnlineAppointment
    private String address;  // Dành cho OfflineAppointment
    private CounselorAppointmentDTO counselorInfo;  // Dành cho counselor
    private StudentAppointmentDTO studentInfo;  // Dành cho student
}
package com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CounselorAppointmentDTO {
    private String fullName;
    private String phoneNumber;
    private Long dateOfBirth;
    private String avatarLink;
}

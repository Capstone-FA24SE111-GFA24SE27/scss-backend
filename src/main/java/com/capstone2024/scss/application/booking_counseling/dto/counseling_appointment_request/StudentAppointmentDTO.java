package com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request;

import com.capstone2024.scss.application.account.dto.ProfileDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentAppointmentDTO {
    private String fullName;
    private String phoneNumber;
    private Long dateOfBirth;
    private String avatarLink;
    private ProfileDTO profile;

    private String studentCode;
}

package com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request;

import com.capstone2024.scss.application.account.dto.ProfileDTO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CounselorAppointmentDTO {
    private String fullName;
    private String phoneNumber;
    private Long dateOfBirth;
    private String avatarLink;
    private ProfileDTO profile;

    private BigDecimal rating;
}

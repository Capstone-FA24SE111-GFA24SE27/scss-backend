package com.capstone2024.scss.application.booking_counseling.dto.counseling_appointment_request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppointmentDetailsDTO {
    private String address;
    private String meetUrl;
}

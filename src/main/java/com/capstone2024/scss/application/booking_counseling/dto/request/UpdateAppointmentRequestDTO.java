package com.capstone2024.scss.application.booking_counseling.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAppointmentRequestDTO {
    private String meetUrl;
    private String address;
}
package com.capstone2024.scss.application.booking_counseling.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelAppointmentRequestDTO {
    @NotBlank(message = "Reason cannot be null or blank")
    private String reason;
}

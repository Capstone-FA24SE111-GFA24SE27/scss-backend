package com.capstone2024.scss.application.booking_counseling.dto.request.counceling_appointment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AppointmentFeedbackDTO {

    @NotNull(message = "Rating cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rating must be greater than 0")
    private BigDecimal rating;

    @NotNull(message = "Comment cannot be null")
    private String comment;
}

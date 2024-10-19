package com.capstone2024.scss.application.booking_counseling.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentFeedbackDTO {

    private Long id;
    private BigDecimal rating;
    private String comment;
    private CounselingAppointmentDTO appointment;
    private Long createdAt;
}

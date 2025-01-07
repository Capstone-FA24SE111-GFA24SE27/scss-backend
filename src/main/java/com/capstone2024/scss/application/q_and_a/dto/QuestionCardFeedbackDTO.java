package com.capstone2024.scss.application.q_and_a.dto;

import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class QuestionCardFeedbackDTO {

    private Long id;
    private BigDecimal rating;
    private String comment;
    private QuestionCardResponseDTO questionCard;
    private Long createdAt;
}

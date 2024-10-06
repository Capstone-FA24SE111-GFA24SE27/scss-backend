package com.capstone2024.scss.application.counseling_appointment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationGoalResponse {
    private String specificGoal;
    private String reason;
}

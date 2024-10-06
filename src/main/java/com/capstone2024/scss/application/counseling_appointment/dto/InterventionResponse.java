package com.capstone2024.scss.application.counseling_appointment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InterventionResponse {
    private String type;
    private String description;
}

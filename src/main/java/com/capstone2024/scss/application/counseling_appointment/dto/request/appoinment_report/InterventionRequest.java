package com.capstone2024.scss.application.counseling_appointment.dto.request.appoinment_report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterventionRequest {
    private String type;
    private String description;
}

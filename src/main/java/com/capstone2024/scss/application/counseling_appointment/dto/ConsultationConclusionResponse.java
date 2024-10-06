package com.capstone2024.scss.application.counseling_appointment.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationConclusionResponse {
    private String counselorConclusion;
    private boolean followUpNeeded;
    private String followUpNotes;
}

package com.capstone2024.scss.application.counseling_appointment.dto.request.appoinment_report;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationContentRequest {
    @NotBlank
    private String summaryOfDiscussion;

    @NotBlank
    private String mainIssues;

    @NotBlank
    private String studentEmotions;

    @NotBlank
    private String studentReactions;
}

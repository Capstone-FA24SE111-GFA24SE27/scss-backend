package com.capstone2024.scss.application.counseling_appointment.dto.request.appoinment_report;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentReportRequest {

    @Valid
    @NotNull
    private ConsultationGoalRequest consultationGoal;

    @Valid
    @NotNull
    private ConsultationContentRequest consultationContent;

    @Valid
    @NotNull
    private ConsultationConclusionRequest consultationConclusion;

    @Valid
    @NotNull
    private InterventionRequest intervention;
}
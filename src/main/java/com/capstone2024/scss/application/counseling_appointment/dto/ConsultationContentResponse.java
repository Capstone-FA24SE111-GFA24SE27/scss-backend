package com.capstone2024.scss.application.counseling_appointment.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationContentResponse {
    private String summaryOfDiscussion;
    private String mainIssues;
    private String studentEmotions;
    private String studentReactions;
}

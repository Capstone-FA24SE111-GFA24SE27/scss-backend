package com.capstone2024.scss.application.counseling_appointment.dto;

import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentReportResponse {
    private Long id;
    private StudentProfileDTO student;
    private CounselorProfileDTO counselor;
    private CounselingAppointmentForReportResponse appointment;
    private ConsultationGoalResponse consultationGoal;
    private ConsultationContentResponse consultationContent;
    private ConsultationConclusionResponse consultationConclusion;
    private InterventionResponse intervention;
}

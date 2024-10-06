package com.capstone2024.scss.domain.common.mapper.appointment_counseling;

import com.capstone2024.scss.application.counseling_appointment.dto.*;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_report.entities.AppointmentReport;

public class AppointmentReportMapper {

    public static AppointmentReportResponse toAppointmentReportResponse(AppointmentReport report) {
        if (report == null) {
            return null;
        }

        return AppointmentReportResponse.builder()
                .id(report.getId())
                .student(StudentProfileMapper.toStudentProfileDTO(report.getStudent()))
                .counselor(CounselorProfileMapper.toCounselorProfileDTO(report.getCounselor()))
                .appointment(CounselingAppointmentMapper.toCounselingAppointmentForReportDTO(report.getCounselingAppointment()))
                .consultationGoal(ConsultationGoalResponse.builder()
                        .specificGoal(report.getConsultationGoal().getSpecificGoal())
                        .reason(report.getConsultationGoal().getReason())
                        .build())
                .consultationContent(ConsultationContentResponse.builder()
                        .summaryOfDiscussion(report.getConsultationContent().getSummaryOfDiscussion())
                        .mainIssues(report.getConsultationContent().getMainIssues())
                        .studentEmotions(report.getConsultationContent().getStudentEmotions())
                        .studentReactions(report.getConsultationContent().getStudentReactions())
                        .build())
                .consultationConclusion(ConsultationConclusionResponse.builder()
                        .counselorConclusion(report.getConsultationConclusion().getCounselorConclusion())
                        .followUpNeeded(report.getConsultationConclusion().isFollowUpNeeded())
                        .followUpNotes(report.getConsultationConclusion().getFollowUpNotes())
                        .build())
                .intervention(InterventionResponse.builder()
                        .type(report.getIntervention().getType())
                        .description(report.getIntervention().getDescription())
                        .build())
                .build();
    }
}

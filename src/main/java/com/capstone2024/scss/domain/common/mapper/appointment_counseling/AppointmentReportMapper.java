package com.capstone2024.scss.domain.common.mapper.appointment_counseling;

import com.capstone2024.scss.application.counseling_appointment.dto.*;
import com.capstone2024.scss.domain.common.mapper.account.CounselorProfileMapper;
import com.capstone2024.scss.domain.common.mapper.student.StudentMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_report.entities.AppointmentReport;

public class AppointmentReportMapper {

    public static AppointmentReportResponse toAppointmentReportResponse(AppointmentReport report) {
        if (report == null) {
            return null;
        }

        return AppointmentReportResponse.builder()
                .id(report.getId())
                .student(StudentMapper.toStudentProfileDTO(report.getStudent()))
                .counselor(CounselorProfileMapper.toCounselorProfileDTO(report.getCounselor()))
                .appointment(CounselingAppointmentMapper.toCounselingAppointmentForReportDTO(report.getCounselingAppointment()))
                .consultationGoal(ConsultationGoalResponse.builder()
                        .specificGoal(report.getSpecificGoal())
                        .reason(report.getReason())
                        .build())
                .consultationContent(ConsultationContentResponse.builder()
                        .summaryOfDiscussion(report.getSummaryOfDiscussion())
                        .mainIssues(report.getMainIssues())
                        .studentEmotions(report.getStudentEmotions())
                        .studentReactions(report.getStudentReactions())
                        .build())
                .consultationConclusion(ConsultationConclusionResponse.builder()
                        .counselorConclusion(report.getCounselorConclusion())
                        .followUpNeeded(report.isFollowUpNeeded())
                        .followUpNotes(report.getFollowUpNotes())
                        .build())
                .intervention(InterventionResponse.builder()
                        .type(report.getInterventionType())
                        .description(report.getInterventionDescription())
                        .build())
                .build();
    }
}

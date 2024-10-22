package com.capstone2024.scss.domain.common.mapper.appointment_counseling;

import com.capstone2024.scss.application.booking_counseling.dto.AppointmentFeedbackDTO;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.AppointmentFeedback;

public class AppointmentFeedbackMapper {
    public static AppointmentFeedbackDTO toNormalDTO(AppointmentFeedback feedback) {
        if (feedback == null) {
            return null;
        }

        return AppointmentFeedbackDTO.builder()
                .id(feedback.getId())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .appointment(CounselingAppointmentMapper.toCounselingAppointmentDTO(feedback.getAppointment()))
                .createdAt(feedback.getCreatedDate())
                .build();
    }

    public static AppointmentFeedbackDTO toAppointmentDTO(AppointmentFeedback feedback) {
        if (feedback == null) {
            return null;
        }

        return AppointmentFeedbackDTO.builder()
                .id(feedback.getId())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedDate())
                .build();
    }
}

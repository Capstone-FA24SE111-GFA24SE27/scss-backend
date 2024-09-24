package com.capstone2024.scss.domain.common.mapper.appointment_counseling;

import com.capstone2024.scss.application.booking_counseling.dto.AppointmentFeedbackDTO;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.AppointmentFeedback;

public class AppointmentFeedbackMapper {
    public static AppointmentFeedbackDTO toDTO(AppointmentFeedback feedback) {
        if (feedback == null) {
            return null;
        }

        return AppointmentFeedbackDTO.builder()
                .id(feedback.getId())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .appointmentId(feedback.getAppointment() != null ? feedback.getAppointment().getId() : null)
                .createdAt(feedback.getCreatedDate())
                .build();
    }
}

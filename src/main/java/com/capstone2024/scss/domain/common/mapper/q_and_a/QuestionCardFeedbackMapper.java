package com.capstone2024.scss.domain.common.mapper.q_and_a;

import com.capstone2024.scss.application.booking_counseling.dto.AppointmentFeedbackDTO;
import com.capstone2024.scss.application.q_and_a.dto.QuestionCardFeedbackDTO;
import com.capstone2024.scss.domain.common.helpers.DateTimeHelper;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.CounselingAppointmentMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.AppointmentFeedback;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionCardFeedback;

public class QuestionCardFeedbackMapper {
    public static QuestionCardFeedbackDTO toNormalDTO(QuestionCardFeedback feedback) {
        if (feedback == null) {
            return null;
        }

        return QuestionCardFeedbackDTO.builder()
                .id(feedback.getId())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .questionCard(QuestionCardMapper.toQuestionCardResponseDto(feedback.getQuestionCard()))
                .createdAt(DateTimeHelper.toMilliseconds(feedback.getCreatedDate()))
                .build();
    }

    public static QuestionCardFeedbackDTO toQuestionCardDTO(QuestionCardFeedback feedback) {
        if (feedback == null) {
            return null;
        }

        return QuestionCardFeedbackDTO.builder()
                .id(feedback.getId())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .createdAt(DateTimeHelper.toMilliseconds(feedback.getCreatedDate()))
                .build();
    }
}

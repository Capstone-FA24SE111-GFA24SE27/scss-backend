package com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RealTimeQuestionDTO {
    private Long counselorId;
    private Long studentId;
    private Type type;

    public enum Type {
        STUDENT_CREATE_NEW,
        COUNSELOR_ANSWER,
        COUNSELOR_CLOSE,
        STUDENT_DELETE, STUDENT_UPDATE, FLAG, REVIEW, STUDENT_CLOSE
    }
}

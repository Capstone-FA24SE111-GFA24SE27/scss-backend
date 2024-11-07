package com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RealTimeAppointmentRequestDTO {
    private Long counselorId;
    private Long studentId;
    private Type type;

    public enum Type {
        COUNSELOR_REJECT,
        COUNSELOR_APPROVE,
        STUDENT_CREATE_NEW_REQUEST,
    }
}

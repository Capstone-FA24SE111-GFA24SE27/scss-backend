package com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RealTimeAppointmentDTO {
    private Long counselorId;
    private Long studentId;
}

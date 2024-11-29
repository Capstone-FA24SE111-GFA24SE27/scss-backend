package com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto;

import com.capstone2024.scss.application.booking_counseling.dto.enums.SlotStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RealTimeCounselingSlotMesageDTO {
    private String dateChange;
    private Long slotId;
    private SlotStatus newStatus;
    private Long counselorId;
    private Long studentId;
}

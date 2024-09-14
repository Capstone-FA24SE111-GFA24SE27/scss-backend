package com.capstone2024.scss.application.booking_counseling.dto;

import com.capstone2024.scss.application.booking_counseling.dto.enums.SlotStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Builder
public class SlotDTO {
    private String slotCode;
    private LocalTime startTime;
    private LocalTime endTime;
    private SlotStatus status; // available, unavailable, expired
    private boolean isMyAppointment;
}

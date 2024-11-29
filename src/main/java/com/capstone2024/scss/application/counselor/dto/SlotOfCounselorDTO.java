package com.capstone2024.scss.application.counselor.dto;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotOfCounselorDTO {
    private Long id;
    private String slotCode;
    private LocalTime startTime;
    private LocalTime endTime;
    private String name;
    private DayOfWeek dayOfWeek;
}

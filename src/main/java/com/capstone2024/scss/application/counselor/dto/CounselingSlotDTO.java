package com.capstone2024.scss.application.counselor.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounselingSlotDTO {
    private Long id;
    private String slotCode;
    private LocalTime startTime;
    private LocalTime endTime;
}

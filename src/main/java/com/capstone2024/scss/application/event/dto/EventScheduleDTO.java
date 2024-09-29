package com.capstone2024.scss.application.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EventScheduleDTO {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int maxParticipants;
    private int currentParticipants;
    private String address;
    private boolean isRegistered = false;
}

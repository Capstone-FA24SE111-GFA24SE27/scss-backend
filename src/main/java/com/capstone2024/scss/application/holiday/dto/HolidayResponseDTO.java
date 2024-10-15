package com.capstone2024.scss.application.holiday.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HolidayResponseDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String name;
}
package com.capstone2024.scss.application.counselor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableDateRangeDTO {
    private LocalDate startDate;  // Start date of the availability
    private LocalDate endDate;    // End date of the availability
}

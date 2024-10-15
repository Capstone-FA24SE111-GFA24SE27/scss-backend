package com.capstone2024.scss.application.holiday.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HolidayUpdateRequestDTO {
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Size(max = 255, message = "Description can have at most 255 characters")
    private String description;

    @NotNull(message = "Name is required")
    @Size(max = 100, message = "Name can have at most 100 characters")
    private String name;
}

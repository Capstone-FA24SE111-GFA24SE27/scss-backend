package com.capstone2024.scss.application.counselor.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class CreateCounselingSlotRequestDTO {

    @NotBlank(message = "Slot code is required")
    private String slotCode;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;
}


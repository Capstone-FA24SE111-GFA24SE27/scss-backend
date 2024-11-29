package com.capstone2024.scss.application.counselor.dto.counseling_slot;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounselingSlotCreateDTO {

    @NotNull
    @Size(min = 1, max = 50)
    private String slotCode;

    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;
}


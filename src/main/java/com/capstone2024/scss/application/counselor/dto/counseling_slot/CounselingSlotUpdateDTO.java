package com.capstone2024.scss.application.counselor.dto.counseling_slot;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounselingSlotUpdateDTO {

    @NotNull
    private Long id;

    @Size(min = 1, max = 50)
    private String slotCode;

    @Size(min = 1, max = 100)
    private String name;

    private LocalTime startTime;

    private LocalTime endTime;
}

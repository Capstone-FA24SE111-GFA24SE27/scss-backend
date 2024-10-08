package com.capstone2024.scss.application.booking_counseling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateCounselingAppointmentRequestDTO {

    @NotBlank(message = "Slot code must not be blank")
    private String slotCode;

    @NotNull(message = "Date must not be null")
    private LocalDate date;

    @NotNull(message = "Online status must not be null")
    private Boolean isOnline;

    @NotBlank(message = "Reason must not be blank")
    @Size(max = 1000, message = "Reason must not exceed 500 characters")
    private String reason;
}

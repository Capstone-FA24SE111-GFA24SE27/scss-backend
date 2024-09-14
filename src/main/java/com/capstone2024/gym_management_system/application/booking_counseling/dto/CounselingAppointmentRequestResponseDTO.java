package com.capstone2024.gym_management_system.application.booking_counseling.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class CounselingAppointmentRequestResponseDTO {

    private Long id;
    private Long createdDate;
    private boolean softDelete;
    private LocalDate requireDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String meetingType;
    private String reason;
    private Long counselorId;
}

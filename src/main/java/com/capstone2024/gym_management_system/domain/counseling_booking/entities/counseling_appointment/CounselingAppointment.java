package com.capstone2024.gym_management_system.domain.counseling_booking.entities.counseling_appointment;

import com.capstone2024.gym_management_system.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.gym_management_system.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "counseling_appointment")
public class CounselingAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CounselingAppointmentStatus status;

    @OneToOne
    @JoinColumn(name = "appointment_request_id", nullable = false)
    private CounselingAppointmentRequest appointmentRequest;
}


package com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment;

import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_report.entities.AppointmentReport;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
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
@Inheritance(strategy = InheritanceType.JOINED)
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
    @JoinColumn(name = "appointment_request_id", nullable = true)
    private CounselingAppointmentRequest appointmentRequest;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, optional = true)
    private AppointmentFeedback feedback;

    @OneToOne(mappedBy = "counselingAppointment", cascade = CascadeType.ALL, optional = true)
    private AppointmentReport report;
}


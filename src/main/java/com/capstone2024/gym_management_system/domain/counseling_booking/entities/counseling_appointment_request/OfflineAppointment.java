package com.capstone2024.gym_management_system.domain.counseling_booking.entities.counseling_appointment_request;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "offline_appointment")
@PrimaryKeyJoinColumn(name = "appointment_request_id")
public class OfflineAppointment extends CounselingAppointmentRequest {
    @Column(name = "address")
    private String address;

    // Getters and Setters
}

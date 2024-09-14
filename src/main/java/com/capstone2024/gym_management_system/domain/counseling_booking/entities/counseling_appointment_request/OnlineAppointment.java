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
@NoArgsConstructor // Ensure no-args constructor is present
@Entity
@Table(name = "online_appointment")
@PrimaryKeyJoinColumn(name = "appointment_request_id")
public class OnlineAppointment extends CounselingAppointmentRequest {

    @Column(name = "meet_url")
    private String meetUrl;

    // Getters and Setters are provided by Lombok
}

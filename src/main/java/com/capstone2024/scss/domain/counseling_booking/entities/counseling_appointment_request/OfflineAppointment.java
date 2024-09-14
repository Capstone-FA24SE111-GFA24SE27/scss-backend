package com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request;

import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "offline_appointment")
@PrimaryKeyJoinColumn(name = "appointment_id")
public class OfflineAppointment extends CounselingAppointment {
    @Column(name = "address")
    private String address;

    // Getters and Setters
}

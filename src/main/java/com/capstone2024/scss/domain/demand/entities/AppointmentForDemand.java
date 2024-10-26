package com.capstone2024.scss.domain.demand.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "appointment_for_demand")
public class AppointmentForDemand extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counseling_demand_id", nullable = false)
    private CounselingDemand counselingDemand;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counseling_appointment_id", nullable = false)
    private CounselingAppointment counselingAppointment;
}


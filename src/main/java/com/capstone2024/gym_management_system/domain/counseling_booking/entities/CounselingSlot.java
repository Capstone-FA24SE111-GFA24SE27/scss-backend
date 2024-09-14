package com.capstone2024.gym_management_system.domain.counseling_booking.entities;


import com.capstone2024.gym_management_system.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "counseling_slot")
public class CounselingSlot extends BaseEntity {

    @Column(name = "slot_code", nullable = false)
    private String slotCode;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
}

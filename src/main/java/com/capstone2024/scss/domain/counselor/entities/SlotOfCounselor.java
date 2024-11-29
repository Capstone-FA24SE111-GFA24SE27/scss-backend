package com.capstone2024.scss.domain.counselor.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.DayOfWeek;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "slot_of_counselor")
public class SlotOfCounselor extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "counselor_id", nullable = false)
    private Counselor counselor;

    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private CounselingSlot counselingSlot;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;
}


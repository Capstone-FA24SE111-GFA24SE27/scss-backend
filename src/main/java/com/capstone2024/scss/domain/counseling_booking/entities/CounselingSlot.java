package com.capstone2024.scss.domain.counseling_booking.entities;


import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.counselor.entities.SlotOfCounselor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "counseling_slot")
public class CounselingSlot extends BaseEntity {

    @Column(name = "slot_code", nullable = false)
    private String slotCode;

    @Column(name = "slot_name", nullable = false)
    private String name;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ManyToMany(mappedBy = "counselingSlots")
    private List<Counselor> counselors;

    @OneToMany(mappedBy = "counselingSlot")
    private List<SlotOfCounselor> slotOfCounselors;
}

package com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_report.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "consultation_conclusion")
public class ConsultationConclusion extends BaseEntity {

    @Column(name = "counselor_conclusion", columnDefinition = "TEXT")
    private String counselorConclusion;

    @Column(name = "follow_up_needed", columnDefinition = "BIT")
    private boolean followUpNeeded;

    @Column(name = "follow_up_notes", columnDefinition = "TEXT")
    private String followUpNotes;
}

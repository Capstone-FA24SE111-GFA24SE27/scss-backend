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
@Table(name = "consultation_goal")
public class ConsultationGoal extends BaseEntity {

    @Column(name = "specific_goal", columnDefinition = "TEXT")
    private String specificGoal;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;
}

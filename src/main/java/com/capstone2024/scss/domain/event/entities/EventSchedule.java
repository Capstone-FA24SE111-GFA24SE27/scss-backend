package com.capstone2024.scss.domain.event.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_schedule")
public class EventSchedule extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false) // Specify nullable constraint if necessary
    private Event event;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "max_participants", nullable = false)
    private int maxParticipants;

    @Column(name = "current_participants")
    private int currentParticipants;
}

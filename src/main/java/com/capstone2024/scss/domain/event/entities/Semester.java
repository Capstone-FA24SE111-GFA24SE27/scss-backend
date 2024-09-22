package com.capstone2024.scss.domain.event.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "semester")
public class Semester extends BaseEntity {

    @Column(name = "semester_code", nullable = false, unique = true)
    private String semesterCode; // Unique code to identify the semester

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @OneToMany(mappedBy = "semester", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Event> events; // One-to-Many relationship with Event

    @OneToMany(mappedBy = "semester", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TrainingPoint> trainingPoints; // One-to-Many relationship with TrainingPoint
}

package com.capstone2024.scss.domain.counselor.entities;

import jakarta.persistence.*;
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
@Table(name = "academic_counselor")
@PrimaryKeyJoinColumn(name = "counselor_id")
public class AcademicCounselor extends Counselor {

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;

    @Column(name = "academic_degree", nullable = false)
    private String academicDegree; // Bằng cấp học thuật
}

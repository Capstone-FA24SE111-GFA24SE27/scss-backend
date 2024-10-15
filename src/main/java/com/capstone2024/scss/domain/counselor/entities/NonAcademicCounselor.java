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
@Table(name = "non_academic_counselor")
@PrimaryKeyJoinColumn(name = "counselor_id")
public class NonAcademicCounselor extends Counselor {

    @ManyToOne
    @JoinColumn(name = "expertise_id", nullable = false)
    private Expertise expertise;

    @Column(name = "industry_experience", nullable = false)
    private Integer industryExperience;
}

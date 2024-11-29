package com.capstone2024.scss.domain.counselor.entities;

import com.capstone2024.scss.domain.student.entities.Department;
import com.capstone2024.scss.domain.student.entities.Major;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "major_id", nullable = false)
    private Major major;

    @Column(name = "academic_degree", nullable = false)
    private String academicDegree; // Bằng cấp học thuật
}

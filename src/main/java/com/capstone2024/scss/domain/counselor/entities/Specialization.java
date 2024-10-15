package com.capstone2024.scss.domain.counselor.entities;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "specialization")
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "specialization")
    private List<AcademicCounselor> academicCounselors;
}

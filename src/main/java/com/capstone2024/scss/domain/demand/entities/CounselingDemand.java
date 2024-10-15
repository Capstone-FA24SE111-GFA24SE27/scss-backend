package com.capstone2024.scss.domain.demand.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.student.entities.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "counseling_demand")
public class CounselingDemand extends BaseEntity {

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "demand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DemandProblemTag> demandProblemTags = new ArrayList<>();

    @Column(name = "total_point", nullable = false)
    private int totalPoint = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // Thêm quan hệ với Student

    public enum Status {
        WAITING, SOLVE
    }
}


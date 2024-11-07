package com.capstone2024.scss.domain.demand.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.common.entity.Semester;
import com.capstone2024.scss.domain.student.entities.Student;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "demand_problem_tag")
public class DemandProblemTag extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "source", columnDefinition = "TEXT", nullable = false)
    private String source;

//    @Column(name = "tag_name", columnDefinition = "TEXT", nullable = false)
//    private String tagName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prob_tag_id", nullable = false)
    private ProblemTag problemTag;

    @Column(name = "number", nullable = false)
    private boolean isExcluded;

//    @Column(name = "total_point", nullable = false)
//    private int totalPoint = 0;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "demand_id", nullable = true)
//    private CounselingDemand demand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = true)
    private Semester semester;
}

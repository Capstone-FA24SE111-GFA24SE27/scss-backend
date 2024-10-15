package com.capstone2024.scss.domain.demand.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
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

    @Column(name = "tag_name", columnDefinition = "TEXT", nullable = false)
    private String tagName;

    @Column(name = "number", nullable = false)
    private int number;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demand_id", nullable = true)
    private CounselingDemand demand;
}

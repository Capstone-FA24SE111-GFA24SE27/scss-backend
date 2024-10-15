package com.capstone2024.scss.domain.demand.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
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
@Table(name = "problem_category")
public class ProblemCategory extends BaseEntity {

    @Column(name = "name", columnDefinition = "TEXT", nullable = false)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProblemTag> problemTags = new ArrayList<>();
}


package com.capstone2024.scss.domain.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "semester")
public class Semester extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100, columnDefinition = "VARCHAR(100)")
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 50, columnDefinition = "VARCHAR(50)")
    private String code;

    @Column(name = "start_date", nullable = false, columnDefinition = "DATE")
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false, columnDefinition = "DATE")
    private LocalDate endDate;
}


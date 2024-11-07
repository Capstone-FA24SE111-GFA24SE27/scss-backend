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
}


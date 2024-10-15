package com.capstone2024.scss.domain.student.entities;

import com.capstone2024.scss.domain.account.entities.Profile;
import com.capstone2024.scss.domain.counselor.entities.Specialization;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student")
@PrimaryKeyJoinColumn(name = "profile_id")
public class Student extends Profile {

    @Column(name = "student_code", nullable = false, unique = true)
    private String studentCode;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "specialization_id", nullable = true)
    private Specialization specialization;
}

package com.capstone2024.scss.domain.demand.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.support_staff.entity.SupportStaff;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_following", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "staff_id"})
})
public class StudentFollowing extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private SupportStaff supportStaff;

    @Column(name = "follow_date", nullable = false)
    private LocalDateTime followDate;

    @Column(name = "follow_note", columnDefinition = "TEXT")
    private String followNote;
}


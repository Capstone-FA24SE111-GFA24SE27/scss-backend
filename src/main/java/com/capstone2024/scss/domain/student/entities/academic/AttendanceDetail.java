package com.capstone2024.scss.domain.student.entities.academic;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.student.entities.academic.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attendance_detail")
public class AttendanceDetail extends BaseEntity {

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "slot", nullable = false)
    private String slot;

    @Column(name = "room", nullable = false)
    private String room;

    @Column(name = "lecturer", nullable = false)
    private String lecturer;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;

    @Column(name = "lecturer_comment")
    private String lecturerComment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_study_id", nullable = false)
    private StudentStudy studentStudy;
}

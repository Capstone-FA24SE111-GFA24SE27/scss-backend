package com.capstone2024.scss.domain.student.entities.academic;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.common.entity.Semester;
import com.capstone2024.scss.domain.student.entities.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_study")
public class StudentStudy extends BaseEntity {
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "total_slot", nullable = false)
    private int totalSlot;

    @Column(name = "grade", nullable = true)
    private BigDecimal finalGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(name = "subject_code", nullable = false)
    private String subjectCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StudyStatus status;

    @OneToMany(mappedBy = "studentStudy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttendanceDetail> attendanceDetails = new ArrayList<>();

    public enum StudyStatus {
        NOT_STARTED,
        STUDYING,
        PASSED,
        NOT_PASSED
    }
}

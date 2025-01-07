package com.capstone2024.scss.domain.student.entities.academic;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.common.entity.Semester;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.entities.academic.enums.StudyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "academic_transcript")
public class AcademicTranscript extends BaseEntity {
    @Column(name = "grade", nullable = true)
    private BigDecimal grade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StudyStatus status;

    @Column(name = "term", nullable = false)
    private Integer term;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(name = "subject_code", nullable = false)
    private String subjectCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id", nullable = true)
    private Semester semester;
}

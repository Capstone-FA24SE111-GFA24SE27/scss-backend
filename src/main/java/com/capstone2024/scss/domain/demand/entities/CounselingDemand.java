package com.capstone2024.scss.domain.demand.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.common.entity.Semester;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.support_staff.entity.SupportStaff;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "counseling_demand")
public class CounselingDemand extends BaseEntity {

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

//    @OneToMany(mappedBy = "demand", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<DemandProblemTag> demandProblemTags = new ArrayList<>();

//    @Column(name = "total_point", nullable = false)
//    private int totalPoint = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = true)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private SupportStaff supportStaff;

    @Column(name = "contact_note", nullable = true, columnDefinition = "TEXT")
    private String contactNote;

    @Column(name = "summarize_note", nullable = true, columnDefinition = "TEXT")
    private String summarizeNote;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "counselor_id")
    private Counselor counselor;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "semester_id", nullable = true)
//    private Semester semester;

    @Column(name = "start_datetime", nullable = true)
    private LocalDateTime startDateTime;

    @Column(name = "end_datetime", nullable = true)
    private LocalDateTime endDateTime;

    @OneToMany(mappedBy = "counselingDemand", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<AppointmentForDemand> appointmentsForDemand = new ArrayList<>();

    public enum Status {
        WAITING, PROCESSING, SOLVE
    }
}


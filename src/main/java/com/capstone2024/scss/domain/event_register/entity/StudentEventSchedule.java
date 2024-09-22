package com.capstone2024.scss.domain.event_register.entity;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.event.entities.EventSchedule;
import com.capstone2024.scss.domain.event.entities.enums.AttendanceStatus;
import com.capstone2024.scss.domain.student.entities.Student;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_event_schedule")
public class StudentEventSchedule extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "event_schedule_id", nullable = false)
    private EventSchedule eventSchedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus attendanceStatus;
}

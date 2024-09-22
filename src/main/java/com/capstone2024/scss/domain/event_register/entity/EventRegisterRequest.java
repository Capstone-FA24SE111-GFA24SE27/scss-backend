package com.capstone2024.scss.domain.event_register.entity;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.event.entities.EventSchedule;
import com.capstone2024.scss.domain.event.entities.enums.RegisterStatus;
import com.capstone2024.scss.domain.student.entities.Student;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "event_register_request")
public class EventRegisterRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // Tham chiếu đến sinh viên

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_schedule_id", nullable = false)
    private EventSchedule eventSchedule; // Tham chiếu đến lịch sự kiện

    @Enumerated(EnumType.STRING) // Store enum as a string in the database
    @Column(nullable = false)
    private RegisterStatus status; // Trạng thái đăng ký
}
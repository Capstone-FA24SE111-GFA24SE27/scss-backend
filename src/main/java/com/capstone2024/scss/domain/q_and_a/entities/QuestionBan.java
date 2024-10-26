package com.capstone2024.scss.domain.q_and_a.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.student.entities.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_ban")
public class QuestionBan extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // Sinh viên bị khóa

    @Column(name = "ban_start_date", nullable = false)
    private LocalDateTime banStartDate = LocalDateTime.now();

    @Column(name = "ban_end_date", nullable = false)
    private LocalDateTime banEndDate = banStartDate.plusDays(7); // Khóa trong 7 ngày

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @OneToMany(mappedBy = "questionBan", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuestionFlag> questionFlags;
}


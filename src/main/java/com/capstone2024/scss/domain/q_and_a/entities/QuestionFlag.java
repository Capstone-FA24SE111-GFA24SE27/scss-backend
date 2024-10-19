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

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_flag")
public class QuestionFlag extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // Sinh viên bị gắn cờ

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_card_id", nullable = false)
    private QuestionCard questionCard; // Thẻ câu hỏi liên quan đến cờ này

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason; // Lý do gắn cờ

    @Column(name = "flag_date", nullable = false)
    private LocalDateTime flagDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ban_id", nullable = true)
    private QuestionBan questionBan;

}

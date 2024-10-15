package com.capstone2024.scss.domain.q_and_a.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionCardStatus;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionType;
import com.capstone2024.scss.domain.student.entities.Student;
import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_card")
public class QuestionCard extends BaseEntity {

    @Column(name = "answer", nullable = true)
    private String answer; // Tiêu đề của câu hỏi

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // Nội dung câu hỏi

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Column(name = "is_taken", nullable = false)
    private boolean isTaken = false;

    @Column(name = "is_closed", nullable = false)
    private boolean isClosed = false;

    @Column(name = "closed_date")
    private LocalDateTime closedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private QuestionCardStatus status = QuestionCardStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counselor_id", nullable = true)
    private Counselor counselor;

    @OneToOne(mappedBy = "questionCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private ChatSession chatSession;
}

package com.capstone2024.scss.domain.q_and_a.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributionQuestionCard;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.AppointmentFeedback;
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

    @Column(name = "answer", nullable = true, columnDefinition = "TEXT")
    private String answer; // Tiêu đề của câu hỏi

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String title; // Nội dung câu hỏi

    @Column(name = "detail", nullable = false, columnDefinition = "TEXT")
    private String content; // Nội dung câu hỏi

    @Column(name = "review_reason", nullable = true, columnDefinition = "TEXT")
    private String reviewReason; // Nội dung câu hỏi

//    @Column(name = "content", nullable = true, columnDefinition = "TEXT")
//    private String reviewReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

//    @Column(name = "is_taken", nullable = true)
//    private boolean isTaken = false;

    @Column(name = "is_closed", nullable = false)
    private boolean isClosed = false;

    @Column(name = "is_accepted", nullable = false)
    private boolean isAccepted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "public_status", nullable = false)
    private PublicStatus publicStatus;  // Thêm status

    @Column(name = "closed_date")
    private LocalDateTime closedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private QuestionCardStatus status = QuestionCardStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false)
    private QuestionCarDifficultyLevel difficultyLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "counselor_id", nullable = true)
    private Counselor counselor;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "topic_id", nullable = false) // Thêm trường topic_id
//    private Topic topic;

    @OneToOne(mappedBy = "questionCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private ChatSession chatSession;

    @OneToOne(mappedBy = "questionCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private QuestionFlag questionFlag;

    @OneToOne(mappedBy = "questionCard", cascade = CascadeType.ALL, optional = true)
    private QuestionCardFeedback feedback;

    public enum QuestionCarDifficultyLevel {
        Easy,
        Medium,
        Hard
    }

    public enum PublicStatus {
        PENDING,
        HIDE,
        VISIBLE
    }
}

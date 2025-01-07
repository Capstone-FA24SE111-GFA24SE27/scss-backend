package com.capstone2024.scss.domain.q_and_a.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
@Builder
@Entity
@Table(name = "question_card_feedback")
public class QuestionCardFeedback extends BaseEntity {
    @Column(name = "rating", nullable = false)
    private BigDecimal rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @OneToOne(optional = true) // Made nullable
    @JoinColumn(name = "question_card_id", nullable = true) // Made nullable
    private QuestionCard questionCard;

    @ManyToOne(optional = false) // Counselor is required
    @JoinColumn(name = "counselor_id", nullable = false) // Counselor cannot be null
    private Counselor counselor;
}

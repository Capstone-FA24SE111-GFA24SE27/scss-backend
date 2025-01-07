package com.capstone2024.scss.domain.contribution_question_card.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contribution_question_card")
public class ContributionQuestionCard extends BaseEntity {

    @Column(name = "detail", columnDefinition = "TEXT")
    private String question;

    @Column(name = "question")
    private String title;

    @Column(name = "answer", columnDefinition = "TEXT")
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(name = "public_status", nullable = false)
    private PublicStatus publicStatus;  // Thêm status

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "counselor_id", nullable = false)
    private Counselor counselor;  // Liên kết với counselor

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private ContributedQuestionCardCategory category;

    public enum PublicStatus {
        HIDE,
        VISIBLE
    }
}

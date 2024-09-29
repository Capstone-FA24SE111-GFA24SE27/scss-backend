package com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_report.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "consultation_content")
public class ConsultationContent extends BaseEntity {

    @Column(name = "summary_of_discussion", columnDefinition = "TEXT")
    private String summaryOfDiscussion;

    @Column(name = "main_issues", columnDefinition = "TEXT")
    private String mainIssues;

    @Column(name = "student_emotions", columnDefinition = "TEXT")
    private String studentEmotions;

    @Column(name = "student_reactions", columnDefinition = "TEXT")
    private String studentReactions;
}

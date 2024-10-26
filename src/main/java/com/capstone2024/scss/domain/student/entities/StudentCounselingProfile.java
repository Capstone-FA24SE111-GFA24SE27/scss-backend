package com.capstone2024.scss.domain.student.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.student.enums.CounselingProfileStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "student_counseling_profile")
public class StudentCounselingProfile extends BaseEntity {

    @OneToOne(mappedBy = "counselingProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Student student;

    // Tình trạng tâm lý và sức khỏe
    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    // Tình trạng tâm lý và sức khỏe
    @Column(name = "current_health_status", length = 255)
    private String currentHealthStatus;

    @Column(name = "psychological_status", length = 255)
    private String psychologicalStatus;

    @Column(name = "stress_factors", length = 500)
    private String stressFactors;

    // Thông tin học tập
    @Column(name = "academic_difficulties", length = 500)
    private String academicDifficulties;

    @Column(name = "study_plan", length = 500)
    private String studyPlan;

    // Thông tin hướng nghiệp
    @Column(name = "career_goals", length = 500)
    private String careerGoals;

    @Column(name = "part_time_experience", length = 500)
    private String partTimeExperience;

    @Column(name = "internship_program", length = 500)
    private String internshipProgram;

    // Hoạt động và đời sống
    @Column(name = "extracurricular_activities", length = 500)
    private String extracurricularActivities;

    @Column(name = "personal_interests", length = 255)
    private String personalInterests;

    @Column(name = "social_relationships", length = 255)
    private String socialRelationships;

    // Hỗ trợ tài chính
    @Column(name = "financial_situation", length = 255)
    private String financialSituation;

    @Column(name = "financial_support", length = 255)
    private String financialSupport;

    // Yêu cầu tư vấn
//    @Column(name = "counseling_issue", nullable = false, length = 500)
//    private String counselingIssue;
//
//    @Column(name = "counseling_goal", length = 500)
//    private String counselingGoal;

    @Column(name = "desired_counseling_fields", length = 500)
    private String desiredCounselingFields;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CounselingProfileStatus status;
}



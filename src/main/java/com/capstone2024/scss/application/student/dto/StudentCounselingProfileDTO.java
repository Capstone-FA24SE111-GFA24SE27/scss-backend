package com.capstone2024.scss.application.student.dto;

import com.capstone2024.scss.domain.student.enums.CounselingProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentCounselingProfileDTO {

    private String introduction;
    private String currentHealthStatus;
    private String psychologicalStatus;
    private String stressFactors;
    private String academicDifficulties;
    private String studyPlan;
    private String careerGoals;
    private String partTimeExperience;
    private String internshipProgram;
    private String extracurricularActivities;
    private String personalInterests;
    private String socialRelationships;
    private String financialSituation;
    private String financialSupport;
//    private String counselingIssue;
//    private String counselingGoal;
    private String desiredCounselingFields;
    private CounselingProfileStatus status;
}

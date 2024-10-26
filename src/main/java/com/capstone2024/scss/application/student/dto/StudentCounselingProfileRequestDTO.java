package com.capstone2024.scss.application.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentCounselingProfileRequestDTO {

    @NotBlank(message = "Introduction cannot be empty.")
    private String introduction;

    @NotBlank(message = "Current health status cannot be empty.")
    @Size(max = 255, message = "Current health status must be less than 255 characters.")
    private String currentHealthStatus;

    @NotBlank(message = "Psychological status cannot be empty.")
    @Size(max = 255, message = "Psychological status must be less than 255 characters.")
    private String psychologicalStatus;

    @NotBlank(message = "Stress factors cannot be empty.")
    @Size(max = 500, message = "Stress factors must be less than 500 characters.")
    private String stressFactors;

    @NotBlank(message = "Academic difficulties cannot be empty.")
    @Size(max = 500, message = "Academic difficulties must be less than 500 characters.")
    private String academicDifficulties;

    @NotBlank(message = "Study plan cannot be empty.")
    @Size(max = 500, message = "Study plan must be less than 500 characters.")
    private String studyPlan;

    @NotBlank(message = "Career goals cannot be empty.")
    @Size(max = 500, message = "Career goals must be less than 500 characters.")
    private String careerGoals;

    @NotBlank(message = "Part-time experience cannot be empty.")
    @Size(max = 500, message = "Part-time experience must be less than 500 characters.")
    private String partTimeExperience;

    @NotBlank(message = "Internship program cannot be empty.")
    @Size(max = 500, message = "Internship program must be less than 500 characters.")
    private String internshipProgram;

    @NotBlank(message = "Extracurricular activities cannot be empty.")
    @Size(max = 500, message = "Extracurricular activities must be less than 500 characters.")
    private String extracurricularActivities;

    @NotBlank(message = "Personal interests cannot be empty.")
    @Size(max = 255, message = "Personal interests must be less than 255 characters.")
    private String personalInterests;

    @NotBlank(message = "Social relationships cannot be empty.")
    @Size(max = 255, message = "Social relationships must be less than 255 characters.")
    private String socialRelationships;

    @NotBlank(message = "Financial situation cannot be empty.")
    @Size(max = 255, message = "Financial situation must be less than 255 characters.")
    private String financialSituation;

    @NotBlank(message = "Financial support cannot be empty.")
    @Size(max = 255, message = "Financial support must be less than 255 characters.")
    private String financialSupport;

//    @NotBlank(message = "Counseling issue cannot be empty.")
//    @Size(max = 500, message = "Counseling issue must be less than 500 characters.")
//    private String counselingIssue;
//
//    @NotBlank(message = "Counseling goal cannot be empty.")
//    @Size(max = 500, message = "Counseling goal must be less than 500 characters.")
//    private String counselingGoal;

    @NotBlank(message = "Desired counseling fields cannot be empty.")
    @Size(max = 500, message = "Desired counseling fields must be less than 500 characters.")
    private String desiredCounselingFields;
}


package com.capstone2024.scss.application.contribution_question_card.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContributionQuestionCardDTO {

    private String question;
    private String title;
    private String answer;
    private Long categoryId;
    private Long counselorId;

}

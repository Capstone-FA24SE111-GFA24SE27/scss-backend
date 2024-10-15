package com.capstone2024.scss.application.q_and_a.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerQuestionCardRequestDTO {
    @NotBlank(message = "Content must not be blank")
    private String content;
}

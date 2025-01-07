package com.capstone2024.scss.application.q_and_a.dto;

import com.capstone2024.scss.domain.q_and_a.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionCardRequestDTO {

    @NotBlank(message = "Content must not be blank")
    private String content; // Nội dung câu hỏi

    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotNull(message = "Question type must not be null")
    private QuestionType questionType;
//
//    private Long topicId;
//
//    private Long specializationId;
//
    private Long departmentId;

    private Long majorId;

    private Long expertiseId;
}

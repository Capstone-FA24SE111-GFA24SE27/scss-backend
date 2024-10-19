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
public class FlagQuestionCardRequestDTO {

    @NotBlank(message = "Reason cannot be empty")
    private String reason; // Lý do gắn cờ
}

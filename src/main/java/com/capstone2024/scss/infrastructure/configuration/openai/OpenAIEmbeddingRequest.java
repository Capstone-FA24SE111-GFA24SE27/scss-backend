package com.capstone2024.scss.infrastructure.configuration.openai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIEmbeddingRequest {
    private String model;
    private String input;
}

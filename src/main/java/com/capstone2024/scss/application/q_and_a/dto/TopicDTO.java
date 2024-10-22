package com.capstone2024.scss.application.q_and_a.dto;

import com.capstone2024.scss.domain.q_and_a.enums.TopicType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopicDTO {
    private Long id;          // ID của chủ đề
    private String name;      // Tên của chủ đề
    private TopicType type;   // Loại của chủ đề (academic hoặc non-academic)
}

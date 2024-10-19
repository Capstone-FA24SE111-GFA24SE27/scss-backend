package com.capstone2024.scss.application.q_and_a.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanInformationResponseDTO {
    private boolean isBan;
    private LocalDateTime banStartDate; // Ngày bắt đầu khóa
    private LocalDateTime banEndDate; // Ngày kết thúc khóa
    private List<QuestionFlagResponseDTO> questionFlags; // Danh sách cờ liên quan

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionFlagResponseDTO {
        private Long id; // ID của QuestionFlag
        private String reason; // Lý do gắn cờ
        private LocalDateTime flagDate; // Ngày gắn cờ
        private QuestionCardResponseDTO questionCard;
    }
}

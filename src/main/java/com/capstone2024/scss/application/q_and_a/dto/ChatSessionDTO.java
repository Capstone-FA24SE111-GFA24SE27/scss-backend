package com.capstone2024.scss.application.q_and_a.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSessionDTO {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime lastInteractionDate;
    private boolean isClosed;
    private List<MessageDTO> messages;
}

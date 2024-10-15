package com.capstone2024.scss.application.q_and_a.dto;

import com.capstone2024.scss.application.authentication.dto.AccountDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long chatSessionId;
    private Long id;
    private AccountDTO sender;
    private String content;
    private String sentAt;
    private boolean isRead;
}

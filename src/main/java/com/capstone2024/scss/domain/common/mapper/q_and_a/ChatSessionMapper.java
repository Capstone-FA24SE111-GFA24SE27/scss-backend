package com.capstone2024.scss.domain.common.mapper.q_and_a;

import com.capstone2024.scss.application.q_and_a.dto.ChatSessionDTO;
import com.capstone2024.scss.application.q_and_a.dto.MessageDTO;
import com.capstone2024.scss.domain.common.mapper.account.AccountMapper;
import com.capstone2024.scss.domain.q_and_a.entities.ChatSession;
import com.capstone2024.scss.domain.q_and_a.entities.Message;

import java.util.List;
import java.util.stream.Collectors;

public class ChatSessionMapper {

    /**
     * Chuyển đổi ChatSession entity sang ChatSessionDTO.
     *
     * @param chatSession ChatSession entity
     * @return ChatSessionDTO
     */
    public static ChatSessionDTO toChatSessionDTO(ChatSession chatSession) {
        if (chatSession == null) {
            return null;
        }

        List<MessageDTO> messageDTOs = chatSession.getMessages().stream()
                .map(ChatSessionMapper::toMessageDTO)
                .collect(Collectors.toList());

        return ChatSessionDTO.builder()
                .id(chatSession.getId())
                .startDate(chatSession.getStartDate())
                .lastInteractionDate(chatSession.getLastInteractionDate())
                .isClosed(chatSession.isClosed())
                .messages(messageDTOs)
                .build();
    }

    /**
     * Chuyển đổi Message entity sang MessageDTO.
     *
     * @param message Message entity
     * @return MessageDTO
     */
    public static MessageDTO toMessageDTO(Message message) {
        if (message == null) {
            return null;
        }

        return MessageDTO.builder()
                .id(message.getId())
                .sender(AccountMapper.toAccountDTO(message.getSender()))
                .content(message.getContent())
                .sentAt(message.getSentAt().toString())
                .isRead(message.isRead())
                .build();
    }

    public static MessageDTO toMessageDTODtoWithSessionId(Message message, Long chatSessionId) {
        if (message == null) {
            return null;
        }

        return MessageDTO.builder()
                .id(message.getId())
                .sender(AccountMapper.toAccountDTO(message.getSender()))
                .content(message.getContent())
                .sentAt(message.getSentAt().toString())
                .isRead(message.isRead())
                .chatSessionId(chatSessionId)
                .build();
    }
}

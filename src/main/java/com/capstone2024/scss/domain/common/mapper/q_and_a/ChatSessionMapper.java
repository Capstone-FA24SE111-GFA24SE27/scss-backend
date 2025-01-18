package com.capstone2024.scss.domain.common.mapper.q_and_a;

import com.capstone2024.scss.application.q_and_a.dto.ChatSessionDTO;
import com.capstone2024.scss.application.q_and_a.dto.MessageDTO;
import com.capstone2024.scss.domain.common.helpers.DateTimeHelper;
import com.capstone2024.scss.domain.common.mapper.account.AccountMapper;
import com.capstone2024.scss.domain.q_and_a.entities.ChatSession;
import com.capstone2024.scss.domain.q_and_a.entities.Message;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

        // Lấy thời gian gốc từ message (giả sử là UTC)
        LocalDateTime sentAtUtc = message.getSentAt();

// Chuyển đổi từ UTC sang múi giờ Việt Nam
        ZoneId utcZoneId = ZoneId.of("UTC");
        ZoneId vietnamZoneId = ZoneId.of("Asia/Ho_Chi_Minh");

// Tạo ZonedDateTime từ UTC
        ZonedDateTime zonedDateTimeUtc = sentAtUtc.atZone(utcZoneId);

// Chuyển sang múi giờ Việt Nam
        ZonedDateTime zonedDateTimeVietnam = zonedDateTimeUtc.withZoneSameInstant(vietnamZoneId);

// Định dạng chuỗi
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        String sendAt = zonedDateTimeVietnam.format(formatter);

        return MessageDTO.builder()
                .id(message.getId())
                .sender(AccountMapper.toAccountDTO(message.getSender()))
                .content(message.getContent())
                .sentAt(sendAt)
                .isRead(message.isRead())
                .build();
    }

    public static MessageDTO toMessageDTODtoWithSessionId(Message message, Long chatSessionId) {
        if (message == null) {
            return null;
        }

        // Lấy thời gian gốc từ message (giả sử là UTC)
        LocalDateTime sentAtUtc = message.getSentAt();

// Chuyển đổi từ UTC sang múi giờ Việt Nam
        ZoneId utcZoneId = ZoneId.of("UTC");
        ZoneId vietnamZoneId = ZoneId.of("Asia/Ho_Chi_Minh");

// Tạo ZonedDateTime từ UTC
        ZonedDateTime zonedDateTimeUtc = sentAtUtc.atZone(utcZoneId);

// Chuyển sang múi giờ Việt Nam
        ZonedDateTime zonedDateTimeVietnam = zonedDateTimeUtc.withZoneSameInstant(vietnamZoneId);

// Định dạng chuỗi
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        String sendAt = zonedDateTimeVietnam.format(formatter);

        return MessageDTO.builder()
                .id(message.getId())
                .sender(AccountMapper.toAccountDTO(message.getSender()))
                .content(message.getContent())
                .sentAt(sendAt)
                .isRead(message.isRead())
                .chatSessionId(chatSessionId)
                .build();
    }
}

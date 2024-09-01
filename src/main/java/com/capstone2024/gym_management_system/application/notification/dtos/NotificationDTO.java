package com.capstone2024.gym_management_system.application.notification.dtos;

import com.capstone2024.gym_management_system.domain.account.entities.Account;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationDTO {
    private Long receiverId;

    private String title;

    private String message;

    private boolean readStatus;

    private String sender;
}

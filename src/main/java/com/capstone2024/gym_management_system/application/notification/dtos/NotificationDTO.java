package com.capstone2024.gym_management_system.application.notification.dtos;

import com.capstone2024.gym_management_system.domain.account.entities.Account;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO implements Serializable {
    private Long receiverId;

    private String title;

    private String message;

    private boolean readStatus;

    private String sender;
}

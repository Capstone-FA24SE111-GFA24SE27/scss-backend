package com.capstone2024.scss.application.notification.dtos;

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
    private Long notificationId;

    private Long receiverId;

    private String title;

    private String message;

    private boolean readStatus;

    private String sender;
}

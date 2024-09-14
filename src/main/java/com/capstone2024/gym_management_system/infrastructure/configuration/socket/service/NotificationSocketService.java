package com.capstone2024.gym_management_system.infrastructure.configuration.socket.service;

import com.capstone2024.gym_management_system.application.notification.dtos.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotificationToUser(Long accountId, NotificationDTO notificationMessage) {
        System.out.println(String.valueOf(accountId));
        messagingTemplate.convertAndSendToUser(
                "2",
                "/private/notification",
                notificationMessage
        );
    }
}

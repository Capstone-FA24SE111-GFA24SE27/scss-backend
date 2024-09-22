package com.capstone2024.scss.infrastructure.configuration.rabbitmq.receiver;

import com.capstone2024.scss.application.notification.dtos.NotificationDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.notification.entities.Notification;
import com.capstone2024.scss.infrastructure.configuration.socket.service.NotificationSocketService;
import com.capstone2024.scss.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.scss.infrastructure.repositories.NotificationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RabbitMQNotificationReceiver {

    private final NotificationSocketService notificationSocketService;

    public RabbitMQNotificationReceiver(NotificationSocketService notificationSocketService) {
        this.notificationSocketService = notificationSocketService;
    }

    public void receiveNotificationMessage(NotificationDTO notificationMessage) {
        notificationSocketService.sendNotificationToUser(notificationMessage.getReceiverId(), notificationMessage);
        System.out.println("Notification sent to WebSocket successfully!");
    }

}

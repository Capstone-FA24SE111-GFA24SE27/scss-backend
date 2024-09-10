package com.capstone2024.gym_management_system.infrastructure.configuration.rabbitmq.receiver;

import com.capstone2024.gym_management_system.application.notification.dtos.NotificationDTO;
import com.capstone2024.gym_management_system.domain.account.entities.Account;
import com.capstone2024.gym_management_system.domain.notification.entities.Notification;
import com.capstone2024.gym_management_system.infrastructure.configuration.socket.service.NotificationSocketService;
import com.capstone2024.gym_management_system.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.gym_management_system.infrastructure.repositories.notification.NotificationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RabbitMQNotificationReceiver {

    private final NotificationSocketService notificationSocketService;
    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    public RabbitMQNotificationReceiver(NotificationSocketService notificationSocketService, NotificationRepository notificationRepository, AccountRepository accountRepository) {
        this.notificationSocketService = notificationSocketService;
        this.notificationRepository = notificationRepository;
        this.accountRepository = accountRepository;
    }

    public void receiveNotificationMessage(NotificationDTO notificationMessage) {
        try {
            persistNotification(notificationMessage);
            notificationSocketService.sendNotificationToUser(1L, notificationMessage);
            System.out.println("Notification sent to WebSocket successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void persistNotification(NotificationDTO notificationMessage) {
        Optional<Account> accountOptional = accountRepository.findById(notificationMessage.getReceiverId());

        if (accountOptional.isPresent()) {
            Notification notification = Notification.builder()
                    .title(notificationMessage.getTitle())
                    .message(notificationMessage.getMessage())
                    .sender(notificationMessage.getSender())
                    .receiver(accountOptional.get())
                    .build();
            notificationRepository.save(notification);
        }
    }
}

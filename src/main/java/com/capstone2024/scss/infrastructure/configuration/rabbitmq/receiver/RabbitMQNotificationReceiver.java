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
            notificationSocketService.sendNotificationToUser(2L, notificationMessage);
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

package com.capstone2024.scss.domain.notification.services.impl;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.notification.dtos.NotificationDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.common.helpers.DateTimeHelper;
import com.capstone2024.scss.domain.notification.entities.Notification;
import com.capstone2024.scss.domain.notification.services.NotificationService;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.RabbitMQConfig;
import com.capstone2024.scss.infrastructure.configuration.socket.service.NotificationSocketService;
import com.capstone2024.scss.infrastructure.repositories.NotificationRepository;
import com.capstone2024.scss.infrastructure.repositories.account.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AccountRepository accountRepository;
    private final NotificationSocketService notificationSocketService;

    public NotificationServiceImpl(NotificationRepository notificationRepository, RabbitTemplate rabbitTemplate, AccountRepository accountRepository, NotificationSocketService notificationSocketService) {
        this.notificationRepository = notificationRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.accountRepository = accountRepository;
        this.notificationSocketService = notificationSocketService;
    }

    @Override
    public PaginationDTO<List<NotificationDTO>> getAllNotifications(int page,
                                                                    int size,
                                                                    SortDirection sortDirection,
                                                                    String sortBy,
                                                                    Account receiver) {
        logger.debug("Entering getAllNotifications method with parameters - Page: {}, Size: {}, SortDirection: {}, SortBy: {}, Receiver: {}", page, size, sortDirection, sortBy, receiver.getUsername());

        Sort sort = Sort.by(sortBy);
        sort = sortDirection == SortDirection.ASC ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Notification> notificationsPage = notificationRepository.findByReceiver(receiver, pageable);

        List<NotificationDTO> notificationDTOs = notificationsPage.getContent().stream()
                .map(notification -> NotificationDTO.builder()
                        .notificationId(notification.getId())
                        .receiverId(notification.getReceiver().getId())
                        .message(notification.getMessage())
                        .title(notification.getTitle())
                        .readStatus(notification.isReadStatus())
                        .sender(notification.getSender())
                        .createdDate(DateTimeHelper.toMilliseconds(notification.getCreatedDate()))
                        .build()
                )
                .collect(Collectors.toList());

        PaginationDTO<List<NotificationDTO>> responseDTO = PaginationDTO.<List<NotificationDTO>>builder()
                .data(notificationDTOs)
                .totalPages(notificationsPage.getTotalPages())
                .totalElements((int) notificationsPage.getTotalElements())
                .build();

        logger.debug("Exiting getAllNotifications method. Total pages: {}, Total elements: {}", responseDTO.getTotalPages(), responseDTO.getTotalElements());

        return responseDTO;
    }

    @Transactional
    @Override
    public String readNotification(Account principle, Long notificationId) {
        logger.debug("Entering readNotification method with parameters - Principle: {}, NotificationId: {}", principle.getUsername(), notificationId);

        Optional<Notification> notificationOptional = notificationRepository.findByReceiverAndId(principle, notificationId);

        if(notificationOptional.isEmpty()) {
            logger.error("Notification not found for Principle: {}, NotificationId: {}", principle.getUsername(), notificationId);
            throw new BadRequestException("Notification Not Found");
        }

        Notification notification = notificationOptional.get();
        notification.setReadStatus(true);

        notificationRepository.save(notification);

        logger.info("Notification marked as read - Principle: {}, NotificationId: {}", principle.getUsername(), notificationId);

        return "notification is read";
    }

    public void sendNotification(NotificationDTO notificationMessage) {

        try {
            Notification notification = persistNotification(notificationMessage);
            if(notification != null) {
                rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE, buildNotificationDTO(notification));

                rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_MOBILE_QUEUE, buildNotificationDTO(notification));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private NotificationDTO buildNotificationDTO(Notification notification) {
        return NotificationDTO.builder()
                .notificationId(notification.getId())
                .receiverId(notification.getReceiver().getId())
                .message(notification.getMessage())
                .readStatus(notification.isReadStatus())
                .title(notification.getTitle())
                .sender(notification.getSender())
                .createdDate(DateTimeHelper.toMilliseconds(notification.getCreatedDate()))
                .build();
    }

    private Notification persistNotification(NotificationDTO notificationMessage) {
        Optional<Account> accountOptional = accountRepository.findById(notificationMessage.getReceiverId());

        if (accountOptional.isPresent()) {
            Notification notification = Notification.builder()
                    .title(notificationMessage.getTitle())
                    .message(notificationMessage.getMessage())
                    .sender(notificationMessage.getSender())
                    .receiver(accountOptional.get())
                    .build();
            return notificationRepository.save(notification);
        }

        return null;
    }

    @Transactional
    public int markAllAsRead(Long receiverId) {
        return notificationRepository.markAllAsRead(receiverId);
    }
}

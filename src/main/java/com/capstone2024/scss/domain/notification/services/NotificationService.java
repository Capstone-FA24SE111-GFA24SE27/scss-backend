package com.capstone2024.scss.domain.notification.services;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.notification.dtos.NotificationDTO;
import com.capstone2024.scss.domain.account.entities.Account;

import java.util.List;

public interface NotificationService {
    PaginationDTO<List<NotificationDTO>> getAllNotifications(int page,
                                                             int size,
                                                             SortDirection sortDirection,
                                                             String sortBy,
                                                             Account receiver);

    String readNotification(Account principle, Long notificationId);
    int markAllAsRead(Long receiverId);
}

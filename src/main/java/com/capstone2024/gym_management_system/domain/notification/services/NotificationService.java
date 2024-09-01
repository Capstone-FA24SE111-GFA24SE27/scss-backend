package com.capstone2024.gym_management_system.domain.notification.services;

import com.capstone2024.gym_management_system.application.account.dto.enums.SortDirection;
import com.capstone2024.gym_management_system.application.authentication.dto.AccountDTO;
import com.capstone2024.gym_management_system.application.common.dto.PaginationDTO;
import com.capstone2024.gym_management_system.application.notification.dtos.NotificationDTO;
import com.capstone2024.gym_management_system.domain.account.entities.Account;
import com.capstone2024.gym_management_system.domain.notification.entities.Notification;

import java.util.List;

public interface NotificationService {
    PaginationDTO<List<NotificationDTO>> getAllNotifications(int page,
                                                             int size,
                                                             SortDirection sortDirection,
                                                             String sortBy,
                                                             Account receiver);

    String readNotification(Account principle, Long notificationId);
}

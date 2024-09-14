package com.capstone2024.scss.infrastructure.repositories;

import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.notification.entities.Notification;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByReceiver(Account receiver, Pageable pageable);
    Optional<Notification> findByReceiverAndId(Account receiver, Long id);
    @Modifying
    @Query("UPDATE Notification n SET n.readStatus = true WHERE n.receiver.id = :receiverId")
    int markAllAsRead(@Param("receiverId") Long receiverId);
}
package com.capstone2024.scss.domain.notification.entities;

import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification")
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Account receiver;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "read_status", nullable = false)
    private boolean readStatus = false;

    @Column(name = "sender", nullable = false)
    private String sender;

    @PrePersist
    protected void onCreate() {
        super.onCreate();
    }
}

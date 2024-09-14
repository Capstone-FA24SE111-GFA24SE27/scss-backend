package com.capstone2024.gym_management_system.domain.account.entities;

import com.capstone2024.gym_management_system.domain.common.entity.BaseEntity;
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
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "profile")
public class Profile extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "avatar_link", nullable = true)
    private String avatarLink;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "date_of_birth")
    private Long dateOfBirth;

    @PrePersist
    protected void onCreate() {
        super.onCreate();
    }
}

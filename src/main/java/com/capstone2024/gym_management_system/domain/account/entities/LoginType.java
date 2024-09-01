package com.capstone2024.gym_management_system.domain.account.entities;

import com.capstone2024.gym_management_system.domain.account.enums.LoginMethod;
import com.capstone2024.gym_management_system.domain.common.entity.BaseEntity;
import com.capstone2024.gym_management_system.infrastructure.converters.LoginMethodConverter;
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
@Table(name = "login_type")
public class LoginType extends BaseEntity {

    @Column(name = "password", unique = false, nullable = true)
    private String password;

    @Convert(converter = LoginMethodConverter.class)
    @Column(name = "method", nullable = false, length = 20)
    private LoginMethod method;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @PrePersist
    protected void onCreate() {
        super.onCreate();
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

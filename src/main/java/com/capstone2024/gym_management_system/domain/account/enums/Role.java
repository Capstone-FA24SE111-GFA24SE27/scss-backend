package com.capstone2024.gym_management_system.domain.account.enums;

public enum Role {

    ADMIN,
    MEMBER;

    public String asSecurityRole() {
        return "ROLE_" + this.name();
    }
}

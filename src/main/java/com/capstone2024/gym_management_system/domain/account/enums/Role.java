package com.capstone2024.gym_management_system.domain.account.enums;

public enum Role {

    ADMINISTRATOR,
    STUDENT,
    COUNSELOR,
    MANAGER,
    SUPPORT_STAFF;

    public String asSecurityRole() {
        return "ROLE_" + this.name();
    }
}

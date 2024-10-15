package com.capstone2024.scss.domain.account.enums;

public enum Role {

    ADMINISTRATOR,
    STUDENT,
    NON_ACADEMIC_COUNSELOR,
    ACADEMIC_COUNSELOR,
    MANAGER,
    SUPPORT_STAFF;

    public String asSecurityRole() {
        return "ROLE_" + this.name();
    }
}

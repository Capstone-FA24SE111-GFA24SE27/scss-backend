package com.capstone2024.gym_management_system.application.authentication.dto;

import com.capstone2024.gym_management_system.application.profile.dto.ProfileDTO;
import com.capstone2024.gym_management_system.domain.account.entities.Account;
import com.capstone2024.gym_management_system.domain.account.enums.Role;
import com.capstone2024.gym_management_system.domain.account.enums.Status;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountDTO {

//    public AccountDTO(Account account) {
//        this.id = account.getId();
//        this.email = account.getEmail();
//        this.role = account.getRole();
//        this.status = account.getStatus();
//        this.profile = null;
//    }

    private Long id;
    private String email;
    private Role role;
    private Status status;
    private ProfileDTO profile;
}

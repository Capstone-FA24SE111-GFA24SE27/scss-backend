package com.capstone2024.gym_management_system.application.authentication.dto;

import com.capstone2024.gym_management_system.application.profile.dto.ProfileDTO;
import com.capstone2024.gym_management_system.domain.account.enums.Role;
import com.capstone2024.gym_management_system.domain.account.enums.Status;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountDTO {
    private Long id;
    private String email;
    private Role role;
    private Status status;
    private ProfileDTO profile;
}

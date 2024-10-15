package com.capstone2024.scss.application.authentication.dto;

import com.capstone2024.scss.application.account.dto.LoginTypeDTO;
import com.capstone2024.scss.application.account.dto.ProfileDTO;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.account.enums.Status;
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
    private LoginTypeDTO loginType;
    private Role role;
    private Status status;
    private Object profile;
}

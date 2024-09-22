package com.capstone2024.scss.domain.common.mapper.account;

import com.capstone2024.scss.application.authentication.dto.AccountDTO;
import com.capstone2024.scss.domain.account.entities.Account;

public class AccountMapper {

    public static AccountDTO toAccountDTO(Account account) {
        if (account == null) {
            return null;
        }

        return AccountDTO.builder()
                .id(account.getId()) // Assuming `BaseEntity` has a getId() method
                .email(account.getEmail())
                .role(account.getRole())
                .status(account.getStatus())
                .profile(ProfileMapper.toProfileDTO(account.getProfile())) // Using ProfileMapper to map Profile to ProfileDTO
                .build();
    }
}

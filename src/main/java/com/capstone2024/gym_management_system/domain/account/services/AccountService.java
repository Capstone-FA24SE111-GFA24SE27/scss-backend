package com.capstone2024.gym_management_system.domain.account.services;

import com.capstone2024.gym_management_system.application.account.dto.request.FilterRequestDTO;
import com.capstone2024.gym_management_system.application.authentication.dto.AccountDTO;
import com.capstone2024.gym_management_system.application.common.dto.PaginationDTO;
import com.capstone2024.gym_management_system.domain.account.entities.Account;

import java.util.List;

public interface AccountService {
    PaginationDTO<List<AccountDTO>> getAccountsWithFilter(FilterRequestDTO filterRequest);

    String blockAccount(Long accountId, Account principal);

    String unblockAccount(Long accountId, Account principal);

    AccountDTO getOne(Long accountId);
}

package com.capstone2024.scss.domain.account.services;

import com.capstone2024.scss.application.account.dto.ChangePasswordDTO;
import com.capstone2024.scss.application.account.dto.request.AccountCreationRequest;
import com.capstone2024.scss.application.account.dto.request.FilterRequestDTO;
import com.capstone2024.scss.application.authentication.dto.AccountDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.domain.account.entities.Account;

import java.util.List;

public interface AccountService {
    PaginationDTO<List<AccountDTO>> getAccountsWithFilter(FilterRequestDTO filterRequest);

    String blockAccount(Long accountId, Account principal);

    String unblockAccount(Long accountId, Account principal);

    AccountDTO getOne(Long accountId);

    Account createAccount(AccountCreationRequest request);

    void changePassword(String email, ChangePasswordDTO changePasswordDTO);
}

package com.capstone2024.scss.domain.account.services.impl;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.account.dto.request.AccountCreationRequest;
import com.capstone2024.scss.application.account.dto.request.FilterRequestDTO;
import com.capstone2024.scss.application.account.dto.request.LoginTypeRequest;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.authentication.dto.AccountDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.account.dto.ProfileDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.entities.LoginType;
import com.capstone2024.scss.domain.account.entities.Profile;
import com.capstone2024.scss.domain.account.enums.LoginMethod;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.account.enums.Status;
import com.capstone2024.scss.domain.account.services.AccountService;
import com.capstone2024.scss.domain.common.mapper.account.AccountMapper;
import com.capstone2024.scss.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.scss.infrastructure.repositories.account.LoginTypeRepository;
import com.capstone2024.scss.infrastructure.repositories.account.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginTypeRepository loginTypeRepository;
    private final ProfileRepository profileRepository;

    @Override
    public PaginationDTO<List<AccountDTO>> getAccountsWithFilter(FilterRequestDTO filterRequest) {
        logger.info("Starting getAccountsWithFilter with filterRequest: {}", filterRequest);

        Sort sort = Sort.by(filterRequest.getSortBy());
        sort = filterRequest.getSoreDirection() == SortDirection.ASC ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(
                filterRequest.getPagination().getPageNumber(),
                filterRequest.getPagination().getPageSize(),
                sort
        );

        logger.debug("Pageable created with pageNumber: {}, pageSize: {}, sort: {}",
                filterRequest.getPagination().getPageNumber(),
                filterRequest.getPagination().getPageSize(),
                sort);

        Page<Account> accountsPage = accountRepository.findAccountsBySearchAndStatus(
                filterRequest.getSearch() != null ? filterRequest.getSearch().trim() : "",
                filterRequest.getStatus() != null ? filterRequest.getStatus().name() : "",
                pageable
        );

        if (accountsPage.isEmpty()) {
            logger.warn("No accounts found for filterRequest: {}", filterRequest);
        } else {
            logger.info("Found {} accounts with total pages: {}", accountsPage.getTotalElements(), accountsPage.getTotalPages());
        }

        List<AccountDTO> accountDTOs = accountsPage.getContent().stream()
                .filter(account -> !account.getRole().name().equals(Role.ADMINISTRATOR.name()))  // Lọc bỏ những tài khoản có role là ADMIN
                .map(AccountMapper::toAccountDTO)
                .collect(Collectors.toList());

        PaginationDTO<List<AccountDTO>> paginationDTO = PaginationDTO.<List<AccountDTO>>builder()
                .data(accountDTOs)
                .totalPages(accountsPage.getTotalPages())
                .totalElements((int) accountsPage.getTotalElements())
                .build();

        logger.info("Completed getAccountsWithFilter with paginationDTO: {}", paginationDTO);

        return paginationDTO;
    }

    @Transactional
    @Override
    public String blockAccount(Long accountId, Account principal) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);

        if (optionalAccount.isEmpty()) {
            throw new NotFoundException("Account not found");
        }

        Account account = optionalAccount.get();

        if(Objects.equals(account.getId(), principal.getId())) {
            throw new BadRequestException("Can not block your account");
        }

        if (account.getStatus() == Status.INACTIVE) {
            throw new BadRequestException("Account is already inactive");
        }

        account.setStatus(Status.INACTIVE);
        accountRepository.save(account);

        return "Account has been blocked";
    }

    @Transactional
    @Override
    public String unblockAccount(Long accountId, Account principal) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);

        if (optionalAccount.isEmpty()) {
            throw new NotFoundException("Account not found");
        }

        Account account = optionalAccount.get();

        if(Objects.equals(account.getId(), principal.getId())) {
            throw new BadRequestException("Can not unblock your account");
        }

        if (account.getStatus() == Status.ACTIVE) {
            throw new BadRequestException("Account is already active");
        }

        account.setStatus(Status.ACTIVE);
        accountRepository.save(account);

        return "Account has been unblocked";
    }

    @Override
    public AccountDTO getOne(Long accountId) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);

        if (optionalAccount.isEmpty()) {
            throw new NotFoundException("Account not found");
        }

        Account account = optionalAccount.get();

        if (account.getRole().name().equals(Role.ADMINISTRATOR.name())) {
            throw new BadRequestException("Cannot retrieve other administrator");
        }

        return AccountMapper.toAccountDTO(account);
    }

    @Override
    public Account createAccount(AccountCreationRequest request) {
        // Kiểm tra xem email đã tồn tại chưa
        if (accountRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Tạo đối tượng Account
        Account account = Account.builder()
                .email(request.getEmail())
                .role(request.getRole() != null ? request.getRole() : Role.STUDENT) // Mặc định là STUDENT
                .status(Status.ACTIVE)
                .build();

        account = accountRepository.save(account);

        // Tạo LoginType với phương thức DEFAULT hoặc theo yêu cầu
        LoginTypeRequest loginDTO = request.getLogin();
        LoginType loginType = LoginType.builder()
                .method(loginDTO.getMethod() != null ? loginDTO.getMethod() : LoginMethod.DEFAULT)
                .password(passwordEncoder.encode(loginDTO.getPassword()))
                .account(account)
                .build();

        loginTypeRepository.save(loginType);

        // Tạo Profile
        ProfileDTO profileDTO = request.getProfile();
        Profile profile = Profile.builder()
                .account(account)
                .fullName(profileDTO.getFullName())
                .gender(profileDTO.getGender())
                .avatarLink(profileDTO.getAvatarLink())
                .phoneNumber(profileDTO.getPhoneNumber())
                .gender(profileDTO.getGender())
                .dateOfBirth(profileDTO.getDateOfBirth())
                .build();

        profileRepository.save(profile);

        return account;
    }
}

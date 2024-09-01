package com.capstone2024.gym_management_system.application.account.controller;

import com.capstone2024.gym_management_system.application.account.dto.enums.SortDirection;
import com.capstone2024.gym_management_system.application.account.dto.request.FilterRequestDTO;
import com.capstone2024.gym_management_system.application.advice.exeptions.BadRequestException;
import com.capstone2024.gym_management_system.application.authentication.dto.AccountDTO;
import com.capstone2024.gym_management_system.application.common.dto.PaginationDTO;
import com.capstone2024.gym_management_system.application.common.utils.ResponseUtil;
import com.capstone2024.gym_management_system.domain.account.entities.Account;
import com.capstone2024.gym_management_system.domain.account.enums.Status;
import com.capstone2024.gym_management_system.domain.account.services.AccountService;
import com.capstone2024.gym_management_system.infrastructure.configuration.socket.service.NotificationSocketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@Tag(name = "account", description = "API for managing user accounts.")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;
    private final NotificationSocketService notificationSocketService;

    public AccountController(AccountService accountService, NotificationSocketService notificationSocketService) {
        this.accountService = accountService;
        this.notificationSocketService = notificationSocketService;
    }

    @GetMapping()
    @Operation(
            summary = "Retrieve accounts with optional filters",
            description = "Fetches a list of accounts with optional filters for search, status, sorting, and pagination.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(name = "search", description = "Search keyword for account names or emails"),
                    @io.swagger.v3.oas.annotations.Parameter(name = "status", description = "Filter by account status"),
                    @io.swagger.v3.oas.annotations.Parameter(name = "SortDirection", description = "Sort direction (ASC or DESC)"),
                    @io.swagger.v3.oas.annotations.Parameter(name = "sortBy", description = "Field to sort by"),
                    @io.swagger.v3.oas.annotations.Parameter(name = "page", description = "Page number for pagination")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully fetched accounts with applied filters.",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PaginationDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters.",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json"
                            )
                    )
            }
    )
    public ResponseEntity<Object> getAccountsWithFilter(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) Status status,
            @RequestParam(name = "SortDirection", defaultValue = "ASC") SortDirection sortDirection,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        logger.debug("Entering getAccountsWithFilter method with parameters - Search: {}, Status: {}, SortDirection: {}, SortBy: {}, Page: {}", search, status, sortDirection, sortBy, page);

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new BadRequestException("Page must be positive (page > 0)", HttpStatus.BAD_REQUEST);
        }

        PaginationDTO<List<AccountDTO>> responseDTO = accountService.getAccountsWithFilter(FilterRequestDTO.builder()
                .search(StringUtils.trimToNull(search))
                .status(status)
                .soreDirection(sortDirection)
                .sortBy(sortBy)
                .pagination(PageRequest.of(page - 1, 10))
                .build());

        logger.debug("Successfully fetched accounts with filter - Total elements: {}", responseDTO.getTotalElements());

        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/{accountId}/block")
    @Operation(
            summary = "Block an account",
            description = "Blocks an account by account ID.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(name = "accountId", description = "ID of the account to block")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Account successfully blocked."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Account not found.",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json"
                            )
                    )
            }
    )
    public ResponseEntity<Object> blockAccount(@PathVariable Long accountId, @AuthenticationPrincipal @NotNull Account principal) {
        logger.info("Blocking account - AccountId: {}, Principal: {}", accountId, principal.getUsername());
        return ResponseUtil.getResponse(accountService.blockAccount(accountId, principal), HttpStatus.OK);
    }

    @PutMapping("/{accountId}/unblock")
    @Operation(
            summary = "Unblock an account",
            description = "Unblocks an account by account ID.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(name = "accountId", description = "ID of the account to unblock")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Account successfully unblocked."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Account not found.",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json"
                            )
                    )
            }
    )
    public ResponseEntity<Object> unblockAccount(@PathVariable Long accountId, @AuthenticationPrincipal @NotNull Account principal) {
        logger.info("Unblocking account - AccountId: {}, Principal: {}", accountId, principal.getUsername());
        return ResponseUtil.getResponse(accountService.unblockAccount(accountId, principal), HttpStatus.OK);
    }
}

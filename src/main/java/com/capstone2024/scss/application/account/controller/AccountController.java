package com.capstone2024.scss.application.account.controller;

import com.capstone2024.scss.application.account.dto.ChangePasswordDTO;
import com.capstone2024.scss.application.account.dto.ForgotPasswordDTO;
import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.account.dto.request.AccountCreationRequest;
import com.capstone2024.scss.application.account.dto.request.FilterRequestDTO;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.authentication.dto.AccountDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.notification.dtos.NotificationDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.account.enums.Status;
import com.capstone2024.scss.domain.account.services.AccountService;
import com.capstone2024.scss.domain.account.services.impl.PasswordResetService;
import com.capstone2024.scss.domain.notification.services.NotificationService;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.RabbitMQConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@Tag(name = "account", description = "API for managing user accounts.")
@RequiredArgsConstructor
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;
    private final NotificationService notificationService;
    private final PasswordResetService passwordResetService;

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
            @RequestParam(name = "search", defaultValue = "", required = false) String search,
            @RequestParam(name = "status", required = false) Status status,
            @RequestParam(name = "SortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "role", required = true) Role role,
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
                .role(role)
                .pagination(PageRequest.of(page - 1, 10))
                .build());

        logger.debug("Successfully fetched accounts with filter - Total elements: {}", responseDTO.getTotalElements());

//        notificationService.sendNotification(NotificationDTO.builder()
//                .receiverId(2L)
//                .message("This is a test message")
//                .title("Test")
//                .sender("TEST")
//                .readStatus(false)
//                .build());

        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{accountId}")
    @Operation(
            summary = "Retrieve a specific account by ID",
            description = "Fetches detailed information about a specific account by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched the account."),
                    @ApiResponse(responseCode = "404", description = "Account not found.")
            }
    )
    public ResponseEntity<Object> getOne(@PathVariable("accountId") Long accountId) {

        AccountDTO responseDTO = accountService.getOne(accountId);

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

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountCreationRequest request, BindingResult errors) {
        if (errors.hasErrors()) {
            logger.warn("Login request failed due to validation errors: {}", errors.getAllErrors());
            throw new BadRequestException("Invalid login request", errors, HttpStatus.BAD_REQUEST);
        }
        Account account = accountService.createAccount(request);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @PutMapping("/change-password")
    public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO,
                                                 @NotNull @AuthenticationPrincipal Account principle) {
        String email = principle.getEmail(); // Lấy email từ thông tin xác thực hiện tại
        accountService.changePassword(email, changePasswordDTO);
        return ResponseUtil.getResponse("Change password successfully", HttpStatus.OK);
    }

    // Endpoint để yêu cầu quên mật khẩu
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        passwordResetService.handleForgotPassword(forgotPasswordDTO.getEmail());
        return ResponseEntity.ok("Password reset link sent to your email");
    }

    // Endpoint để xác thực token và gửi mật khẩu mới
    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token) {
        try {
            passwordResetService.handlePasswordReset(token);
            return ResponseEntity.ok("A new password has been sent to your email");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private final JavaMailSender mailSender;

    @GetMapping("/send-test-email")
    public String sendTestEmail() {
        try {
            // Tạo email đơn giản
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("phattvse170042@fpt.edu.vn");
            message.setSubject("Test Email");
            message.setText("This is a test email sent from Spring Boot application.");

            // Gửi email
            mailSender.send(message);

            return "Test email sent successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send email: " + e.getMessage();
        }
    }
}

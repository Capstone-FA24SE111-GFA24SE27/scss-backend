package com.capstone2024.scss.application.notification.controllers;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.notification.dtos.NotificationDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.notification.services.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@Tag(name = "notification")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping()
    public ResponseEntity<Object> getAll(
            @RequestParam(name = "SortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @AuthenticationPrincipal @NotNull Account principal) {

        if (page < 1) {
            logger.error("Invalid page number: {}", page);
            throw new BadRequestException("Page must be positive (page > 0)", HttpStatus.BAD_REQUEST);
        }

        logger.info("Fetching notifications - Page: {}, SortBy: {}, SortDirection: {}, User: {}", page, sortBy, sortDirection, principal.getUsername());

        PaginationDTO<List<NotificationDTO>> responseDTO = notificationService.getAllNotifications(
                page - 1,
                10,
                sortDirection,
                sortBy,
                principal);

        logger.info("Successfully fetched notifications for User: {}", principal.getUsername());
        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/read/{notificationId}")
    public ResponseEntity<Object> readNotification(
            @AuthenticationPrincipal @NotNull Account principal,
            @PathVariable("notificationId") Long notificationId) {

        logger.info("Marking notification as read - NotificationId: {}, User: {}", notificationId, principal.getUsername());

        String response = notificationService.readNotification(principal, notificationId);

        logger.info("Successfully marked notification as read - NotificationId: {}, User: {}", notificationId, principal.getUsername());
        return ResponseUtil.getResponse(response, HttpStatus.OK);
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Object> markAllAsRead(@AuthenticationPrincipal @NotNull Account principal) {
        int updatedCount = notificationService.markAllAsRead(principal.getId());
        return ResponseUtil.getResponse("Marked " + updatedCount + " notifications as read.", HttpStatus.OK);
    }
}

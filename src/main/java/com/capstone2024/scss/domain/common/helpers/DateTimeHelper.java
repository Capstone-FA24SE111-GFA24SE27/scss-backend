package com.capstone2024.scss.domain.common.helpers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for handling date and time operations with Vietnam timezone (Asia/Ho_Chi_Minh).
 */
public class DateTimeHelper {

    // Vietnam timezone
    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    /**
     * Gets the current date and time in Vietnam timezone.
     *
     * @return the current LocalDateTime in Vietnam timezone.
     */
    public static LocalDateTime getNow() {
        return LocalDateTime.now(VIETNAM_ZONE);
    }

    /**
     * Converts a LocalDateTime to milliseconds (epoch time) in Vietnam timezone.
     *
     * @param dateTime the LocalDateTime to be converted.
     * @return the epoch time in milliseconds.
     */
    public static long toMilliseconds(LocalDateTime dateTime) {
        return dateTime.atZone(VIETNAM_ZONE).toInstant().toEpochMilli();
    }

    /**
     * Converts milliseconds (epoch time) to LocalDateTime in Vietnam timezone.
     *
     * @param milliseconds the epoch time in milliseconds.
     * @return the corresponding LocalDateTime in Vietnam timezone.
     */
    public static LocalDateTime fromMilliseconds(long milliseconds) {
        return Instant.ofEpochMilli(milliseconds)
                .atZone(VIETNAM_ZONE)
                .toLocalDateTime();
    }

    /**
     * Converts LocalDateTime to a string with Vietnam timezone (Asia/Ho_Chi_Minh).
     *
     * @param dateTime the LocalDateTime to be converted.
     * @return the string representation of the LocalDateTime in Vietnam timezone.
     */
    public static String toZoneDateTimeString(LocalDateTime dateTime) {
        ZonedDateTime zonedDateTime = dateTime.atZone(VIETNAM_ZONE);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME; // Default ISO format
        return zonedDateTime.format(formatter);
    }
}

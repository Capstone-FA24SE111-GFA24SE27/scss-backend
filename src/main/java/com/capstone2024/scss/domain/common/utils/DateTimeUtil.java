package com.capstone2024.scss.domain.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Method to format LocalDateTime
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            return dateTime.format(dateTimeFormatter);
        }
        return "";
    }
}

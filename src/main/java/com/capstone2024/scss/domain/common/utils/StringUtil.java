package com.capstone2024.scss.domain.common.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StringUtil {
    public static String getUsernameFromEmail(String email) {
        if (email == null || !email.contains("@")) {
//            throw new IllegalArgumentException("Invalid email format");
            return email;
        }
        return email.substring(0, email.indexOf("@"));
    }

    public static String formatTagsWithDate(List<String> tags, LocalDate localDate) {
        // Định dạng ngày tháng
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = localDate.format(formatter);

        // Ghép chuỗi các tag
        String tagsString = String.join(", ", tags);

        // Kết hợp thành chuỗi cuối cùng
        return String.format("%s - %s", formattedDate, tagsString);
    }
}

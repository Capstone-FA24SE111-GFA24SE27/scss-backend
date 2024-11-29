package com.capstone2024.scss.domain.common.helpers;

import com.capstone2024.scss.application.notification.dtos.NotificationDTO;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.student.entities.Student;

public class NotificationHelper {
    public static String getSenderAsStudent(Student student) {
        return student == null ?
                ""
                :
                "Student: " + student.getFullName() + "-" + student.getStudentCode();
    }

    public static String getSenderAsCounselor(Counselor counselor) {
        return counselor == null ?
                ""
                :
                "Counselor: " + counselor.getFullName();
    }

    public static String getSenderAsSystem() {
        return "SCSS System";
    }

    public static NotificationDTO getNotificationFromStudentToCounselor(String message, String tittle, boolean readStatus, Student student, Counselor counselor) {
        return NotificationDTO.builder()
                .receiverId(counselor.getId())
                .message(message)
                .title(tittle)
                .sender(getSenderAsStudent(student))
                .readStatus(readStatus)
                .build();
    }

    public static NotificationDTO getNotificationFromCounselorToStudent(String message, String tittle, boolean readStatus, Counselor counselor, Student student) {
        return NotificationDTO.builder()
                .receiverId(student.getId())
                .message(message)
                .title(tittle)
                .sender(getSenderAsCounselor(counselor))
                .readStatus(readStatus)
                .build();
    }
}

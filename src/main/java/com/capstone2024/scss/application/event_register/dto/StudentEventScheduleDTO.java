package com.capstone2024.scss.application.event_register.dto;

import com.capstone2024.scss.domain.event.entities.enums.AttendanceStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEventScheduleDTO {
    private Long id;
    private EventScheduleDTO eventSchedule;
    private AttendanceStatus attendanceStatus;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EventScheduleDTO {
        private Long id;
        private String address;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private EventDTO event;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EventDTO {
        private Long id;
        private String title;
        private String displayImage;
    }
}

package com.capstone2024.scss.domain.common.mapper.event;

import com.capstone2024.scss.application.event_register.dto.StudentEventScheduleDTO;
import com.capstone2024.scss.domain.event.entities.Event;
import com.capstone2024.scss.domain.event.entities.EventSchedule;
import com.capstone2024.scss.domain.event_register.entity.StudentEventSchedule;

public class StudentEventScheduleMapper {

    public static StudentEventScheduleDTO toDTO(StudentEventSchedule schedule) {
        if (schedule == null) {
            return null;
        }
        return StudentEventScheduleDTO.builder()
                .id(schedule.getId())
                .eventSchedule(toEventScheduleDTO(schedule.getEventSchedule()))
                .attendanceStatus(schedule.getAttendanceStatus())
                .build();
    }

    private static StudentEventScheduleDTO.EventScheduleDTO toEventScheduleDTO(EventSchedule eventSchedule) {
        return StudentEventScheduleDTO.EventScheduleDTO.builder()
                .id(eventSchedule.getId())
                .address(eventSchedule.getAddress())
                .startDate(eventSchedule.getStartDate())
                .endDate(eventSchedule.getEndDate())
                .event(toEventDTO(eventSchedule.getEvent()))
                .build();
    }

    private static StudentEventScheduleDTO.EventDTO toEventDTO(Event event) {
        return StudentEventScheduleDTO.EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .displayImage(event.getDisplayImage())
                .build();
    }
}

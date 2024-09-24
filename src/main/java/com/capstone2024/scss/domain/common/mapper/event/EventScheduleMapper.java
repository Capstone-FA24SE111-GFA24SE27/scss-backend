package com.capstone2024.scss.domain.common.mapper.event;

import com.capstone2024.scss.application.event.dto.EventScheduleDTO;
import com.capstone2024.scss.domain.event.entities.EventSchedule;

public class EventScheduleMapper {

    public static EventScheduleDTO toDTO(EventSchedule eventSchedule) {
        if (eventSchedule == null) {
            return null;
        }

        return EventScheduleDTO.builder()
                .id(eventSchedule.getId())
                .startDate(eventSchedule.getStartDate())
                .endDate(eventSchedule.getEndDate())
                .maxParticipants(eventSchedule.getMaxParticipants())
                .currentParticipants(eventSchedule.getCurrentParticipants())
                .build();
    }
}

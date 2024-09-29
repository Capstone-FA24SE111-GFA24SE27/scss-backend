package com.capstone2024.scss.domain.common.mapper.event;

import com.capstone2024.scss.application.event.category.dto.CategoryDTO;
import com.capstone2024.scss.application.event.dto.ContentImageDTO;
import com.capstone2024.scss.application.event.dto.EventDTO;
import com.capstone2024.scss.application.event.dto.EventScheduleDTO;
import com.capstone2024.scss.application.event.dto.RecapVideoDTO;
import com.capstone2024.scss.application.event.semester.dto.SemesterDTO;
import com.capstone2024.scss.domain.event.entities.Event;

import java.util.List;
import java.util.stream.Collectors;

public class EventMapper {
    public static EventDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }

        SemesterDTO semesterDTO = SemesterMapper.toDTO(event.getSemester());
        CategoryDTO categoryDTO = CategoryMapper.toDTO(event.getCategory());

        List<RecapVideoDTO> recapVideoDTOs = event.getRecapVideos().stream()
                .map(RecapVideoMapper::toDTO)
                .collect(Collectors.toList());

        List<ContentImageDTO> contentImageDTOs = event.getContentImages().stream()
                .map(ContentImageMapper::toDTO)
                .collect(Collectors.toList());

        List<EventScheduleDTO> eventScheduleDTOs = event.getEventSchedules().stream()
                .map(EventScheduleMapper::toDTO)
                .collect(Collectors.toList());

        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .content(event.getContent())
                .view(event.getView())
                .isNeedAccept(event.getIsNeedAccept())
//                .startDate(event.getStartDate())
//                .endDate(event.getEndDate())
                .displayImage(event.getDisplayImage())
                .semester(semesterDTO)
                .category(categoryDTO)
                .recapVideos(recapVideoDTOs)
                .contentImages(contentImageDTOs)
                .eventSchedules(eventScheduleDTOs)
                .build();
    }
}

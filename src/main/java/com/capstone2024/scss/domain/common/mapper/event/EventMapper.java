package com.capstone2024.scss.domain.common.mapper.event;

import com.capstone2024.scss.application.event.category.dto.CategoryDTO;
import com.capstone2024.scss.application.event.dto.EventDTO;
import com.capstone2024.scss.application.event.semester.dto.SemesterDTO;
import com.capstone2024.scss.domain.event.entities.Event;

public class EventMapper {
    public static EventDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }

        SemesterDTO semesterDTO = SemesterMapper.toDTO(event.getSemester());
        CategoryDTO categoryDTO = CategoryMapper.toDTO(event.getCategory());

        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .address(event.getAddress())
                .content(event.getContent())
                .view(event.getView())
                .isNeedAccept(event.getIsNeedAccept())
                .displayImage(event.getDisplayImage()) // Thêm trường hình ảnh
                .semester(semesterDTO)
                .category(categoryDTO)
                .build();
    }
}

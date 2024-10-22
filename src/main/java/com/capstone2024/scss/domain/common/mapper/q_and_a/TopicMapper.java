package com.capstone2024.scss.domain.common.mapper.q_and_a;

import com.capstone2024.scss.application.q_and_a.dto.TopicDTO;
import com.capstone2024.scss.domain.q_and_a.entities.Topic;

public class TopicMapper {
    public static TopicDTO toDTO(Topic topic) {
        if (topic == null) {
            return null;
        }

        return TopicDTO.builder()
                .id(topic.getId())
                .name(topic.getName())
                .type(topic.getType())
                .build();
    }
}

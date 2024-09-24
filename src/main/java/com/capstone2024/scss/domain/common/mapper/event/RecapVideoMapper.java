package com.capstone2024.scss.domain.common.mapper.event;

import com.capstone2024.scss.application.event.dto.RecapVideoDTO;
import com.capstone2024.scss.domain.event.entities.RecapVideo;

public class RecapVideoMapper {
    public static RecapVideoDTO toDTO(RecapVideo recapVideo) {
        if (recapVideo == null) {
            return null;
        }
        return RecapVideoDTO.builder()
                .id(recapVideo.getId())
                .videoUrl(recapVideo.getVideoUrl())
                .build();
    }
}

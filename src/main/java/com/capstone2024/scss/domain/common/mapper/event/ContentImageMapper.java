package com.capstone2024.scss.domain.common.mapper.event;

import com.capstone2024.scss.application.event.dto.ContentImageDTO;
import com.capstone2024.scss.domain.event.entities.ContentImage;

public class ContentImageMapper {
    public static ContentImageDTO toDTO(ContentImage contentImage) {
        if (contentImage == null) {
            return null;
        }
        return ContentImageDTO.builder()
                .id(contentImage.getId())
                .imageUrl(contentImage.getImageUrl())
                .build();
    }
}

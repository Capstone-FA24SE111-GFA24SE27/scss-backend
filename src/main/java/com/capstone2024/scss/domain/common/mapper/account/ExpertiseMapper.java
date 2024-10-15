package com.capstone2024.scss.domain.common.mapper.account;

import com.capstone2024.scss.application.counselor.dto.ExpertiseDTO;
import com.capstone2024.scss.domain.counselor.entities.Expertise;

public class ExpertiseMapper {
    public static ExpertiseDTO toExpertiseDTO(Expertise expertise) {
        if (expertise == null) {
            return null;
        }

        return ExpertiseDTO.builder()
                .id(expertise.getId())
                .name(expertise.getName())
                .build();
    }
}

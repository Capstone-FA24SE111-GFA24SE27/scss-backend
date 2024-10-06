package com.capstone2024.scss.domain.common.mapper.appointment_counseling;

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

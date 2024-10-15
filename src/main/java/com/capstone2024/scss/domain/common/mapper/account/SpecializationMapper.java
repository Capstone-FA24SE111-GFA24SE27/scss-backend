package com.capstone2024.scss.domain.common.mapper.account;

import com.capstone2024.scss.application.counselor.dto.SpecializationDTO;
import com.capstone2024.scss.domain.counselor.entities.Specialization;

public class SpecializationMapper {
    public static SpecializationDTO toSpecializationDTO(Specialization specialization) {
        if (specialization == null) {
            return null;
        }

        return SpecializationDTO.builder()
                .id(specialization.getId())
                .name(specialization.getName())
                .build();
    }
}

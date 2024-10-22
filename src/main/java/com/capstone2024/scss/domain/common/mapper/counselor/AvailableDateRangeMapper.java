package com.capstone2024.scss.domain.common.mapper.counselor;

import com.capstone2024.scss.application.counselor.dto.AvailableDateRangeDTO;
import com.capstone2024.scss.domain.counselor.entities.AvailableDateRange;

public class AvailableDateRangeMapper {
    public static AvailableDateRangeDTO toAvailableDateRangeDTO(AvailableDateRange availableDateRange) {
        if (availableDateRange == null) {
            return null;
        }

        return AvailableDateRangeDTO.builder()
                .startDate(availableDateRange.getStartDate())
                .endDate(availableDateRange.getEndDate())
                .build();
    }
}

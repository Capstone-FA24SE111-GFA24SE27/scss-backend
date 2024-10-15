package com.capstone2024.scss.domain.common.mapper.holiday;

import com.capstone2024.scss.application.holiday.dto.HolidayResponseDTO;
import com.capstone2024.scss.application.holiday.dto.request.HolidayCreateRequestDTO;
import com.capstone2024.scss.application.holiday.dto.request.HolidayUpdateRequestDTO;
import com.capstone2024.scss.domain.counseling_booking.entities.Holiday;

public class HolidayMapper {

    // Chuyển từ entity Holiday sang HolidayResponseDTO
    public static HolidayResponseDTO toHolidayResponseDTO(Holiday holiday) {
        if (holiday == null) {
            return null;
        }

        return HolidayResponseDTO.builder()
                .id(holiday.getId())
                .startDate(holiday.getStartDate())
                .endDate(holiday.getEndDate())
                .description(holiday.getDescription())
                .name(holiday.getName())
                .build();
    }

    // Chuyển từ HolidayCreateRequestDTO sang entity Holiday
    public static Holiday toHoliday(HolidayCreateRequestDTO createDTO) {
        if (createDTO == null) {
            return null;
        }

        return Holiday.builder()
                .startDate(createDTO.getStartDate())
                .endDate(createDTO.getEndDate())
                .description(createDTO.getDescription())
                .name(createDTO.getName())
                .build();
    }

    // Chuyển từ HolidayUpdateRequestDTO sang entity Holiday
    public static void updateHolidayFromDTO(HolidayUpdateRequestDTO updateDTO, Holiday holiday) {
        if (updateDTO == null || holiday == null) {
            return;
        }

        holiday.setStartDate(updateDTO.getStartDate());
        holiday.setEndDate(updateDTO.getEndDate());
        holiday.setDescription(updateDTO.getDescription());
        holiday.setName(updateDTO.getName());
    }
}

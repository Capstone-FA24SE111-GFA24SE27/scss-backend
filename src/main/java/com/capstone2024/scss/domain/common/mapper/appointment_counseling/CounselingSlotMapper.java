package com.capstone2024.scss.domain.common.mapper.appointment_counseling;

import com.capstone2024.scss.application.counselor.dto.CounselingSlotDTO;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;

public class CounselingSlotMapper {
    public static CounselingSlotDTO toDTO(CounselingSlot slot) {
        if (slot == null) {
            return null;
        }

        return CounselingSlotDTO.builder()
                .id(slot.getId())
                .slotCode(slot.getSlotCode())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .name(slot.getName())
                .build();
    }
}

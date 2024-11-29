package com.capstone2024.scss.domain.common.mapper.appointment_counseling;

import com.capstone2024.scss.application.counselor.dto.CounselingSlotDTO;
import com.capstone2024.scss.application.counselor.dto.SlotOfCounselorDTO;
import com.capstone2024.scss.application.counselor.dto.counseling_slot.CounselingSlotCreateDTO;
import com.capstone2024.scss.application.counselor.dto.counseling_slot.CounselingSlotUpdateDTO;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counselor.entities.SlotOfCounselor;

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

    public static SlotOfCounselorDTO toDTOSlotOfCounselor(SlotOfCounselor slotOfCounselor) {
        if (slotOfCounselor == null) {
            return null;
        }

        return SlotOfCounselorDTO.builder()
                .id(slotOfCounselor.getId())
                .slotCode(slotOfCounselor.getCounselingSlot().getSlotCode())
                .startTime(slotOfCounselor.getCounselingSlot().getStartTime())
                .endTime(slotOfCounselor.getCounselingSlot().getEndTime())
                .name(slotOfCounselor.getCounselingSlot().getName())
                .dayOfWeek(slotOfCounselor.getDayOfWeek())
                .build();
    }

    public static CounselingSlot toEntity(CounselingSlotCreateDTO createDTO) {
        CounselingSlot counselingSlot = new CounselingSlot();
        counselingSlot.setSlotCode(createDTO.getSlotCode());
        counselingSlot.setName(createDTO.getName());
        counselingSlot.setStartTime(createDTO.getStartTime());
        counselingSlot.setEndTime(createDTO.getEndTime());
        return counselingSlot;
    }

    public static void updateEntity(CounselingSlot counselingSlot, CounselingSlotUpdateDTO updateDTO) {
        counselingSlot.setName(updateDTO.getName());
        counselingSlot.setStartTime(updateDTO.getStartTime());
        counselingSlot.setEndTime(updateDTO.getEndTime());
    }
}

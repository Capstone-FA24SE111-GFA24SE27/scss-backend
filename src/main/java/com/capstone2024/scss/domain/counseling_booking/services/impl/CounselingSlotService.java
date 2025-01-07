package com.capstone2024.scss.domain.counseling_booking.services.impl;

import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.counselor.dto.CounselingSlotDTO;
import com.capstone2024.scss.application.counselor.dto.counseling_slot.CounselingSlotCreateDTO;
import com.capstone2024.scss.application.counselor.dto.counseling_slot.CounselingSlotUpdateDTO;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.CounselingSlotMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CounselingSlotService {

    private final CounselingSlotRepository counselingSlotRepository;

    public CounselingSlotDTO createOne(CounselingSlotCreateDTO createDTO) {
        CounselingSlot counselingSlot = CounselingSlotMapper.toEntity(createDTO);
        counselingSlot = counselingSlotRepository.save(counselingSlot);
        return CounselingSlotMapper.toDTO(counselingSlot);
    }

    public CounselingSlotDTO getOne(Long id) {
        CounselingSlot counselingSlot = counselingSlotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Counseling slot not found"));
        return CounselingSlotMapper.toDTO(counselingSlot);
    }

    public CounselingSlotDTO update(Long id, CounselingSlotUpdateDTO updateDTO) {
        CounselingSlot counselingSlot = counselingSlotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Counseling slot not found"));

        // Only allow update if no counselors are assigned
//        if (counselingSlot.getCounselors() == null || counselingSlot.getCounselors().isEmpty()) {
            CounselingSlotMapper.updateEntity(counselingSlot, updateDTO);
            counselingSlot = counselingSlotRepository.save(counselingSlot);
            return CounselingSlotMapper.toDTO(counselingSlot);
//        } else {
//            throw new BadRequestException("Cannot update slot with assigned counselors");
//        }
    }

    public void delete(Long id) {
        CounselingSlot counselingSlot = counselingSlotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Counseling slot not found"));

        // Only allow delete if no counselors are assigned
//        if (counselingSlot.getCounselors() == null || counselingSlot.getCounselors().isEmpty()) {
            counselingSlotRepository.deleteById(id);
//        } else {
//            throw new BadRequestException("Cannot delete slot with assigned counselors");
//        }
    }
}


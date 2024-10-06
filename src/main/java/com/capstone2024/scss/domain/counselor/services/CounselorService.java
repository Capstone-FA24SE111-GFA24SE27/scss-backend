package com.capstone2024.scss.domain.counselor.services;

import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.application.booking_counseling.dto.SlotDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.counselor.dto.CounselingSlotDTO;
import com.capstone2024.scss.application.counselor.dto.CounselorDTO;
import com.capstone2024.scss.application.counselor.dto.request.CounselorFilterRequestDTO;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.ExpertiseDTO;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;

import java.time.LocalDate;
import java.util.List;

public interface CounselorService {
    PaginationDTO<List<CounselorProfileDTO>> getCounselorsWithFilter(CounselorFilterRequestDTO filterRequest);

    CounselorProfileDTO getOneCounselor(Long counselorId);

    List<ExpertiseDTO> getAllExpertises();

    CounselorProfileDTO findBestAvailableCounselor(Long slotId, LocalDate date, Gender gender, Long expertiseId);

    List<SlotDTO> getAllCounselingSlots(LocalDate date, Long studentId);
}

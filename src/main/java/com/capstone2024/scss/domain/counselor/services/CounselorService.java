package com.capstone2024.scss.domain.counselor.services;

import com.capstone2024.scss.application.account.dto.AcademicCounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.NonAcademicCounselorProfileDTO;
import com.capstone2024.scss.application.booking_counseling.dto.SlotDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.dto.SpecializationDTO;
import com.capstone2024.scss.application.counselor.dto.request.AcademicCounselorFilterRequestDTO;
import com.capstone2024.scss.application.counselor.dto.request.CounselorFilterRequestDTO;
import com.capstone2024.scss.application.counselor.dto.ExpertiseDTO;
import com.capstone2024.scss.application.counselor.dto.request.NonAcademicCounselorFilterRequestDTO;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;

import java.time.LocalDate;
import java.util.List;

public interface CounselorService {
    PaginationDTO<List<CounselorProfileDTO>> getCounselorsWithFilter(CounselorFilterRequestDTO filterRequest);

    CounselorProfileDTO getOneCounselor(Long counselorId);

    List<ExpertiseDTO> getAllExpertises();

    List<CounselorProfileDTO> findBestAvailableCounselorForNonAcademic(Long slotId, LocalDate date, Gender gender, String reason, String expertise);

    List<SlotDTO> getAllCounselingSlots(LocalDate date, Long studentId);

    PaginationDTO<List<NonAcademicCounselorProfileDTO>> getNonAcademicCounselorsWithFilter(NonAcademicCounselorFilterRequestDTO filterRequest);

    PaginationDTO<List<AcademicCounselorProfileDTO>> getAcademicCounselorsWithFilter(AcademicCounselorFilterRequestDTO filterRequest);

    List<SpecializationDTO> getAllSpecialization();

    NonAcademicCounselorProfileDTO getNonAcademicCounselorById(Long id);

    AcademicCounselorProfileDTO getAcademicCounselorById(Long id);

    List<CounselorProfileDTO> findBestAvailableCounselorForAcademic(Long slotId, LocalDate date, Gender gender, Long studentId, String reason, Long departmentId, Long majorId, String majorName);

    List<CounselorProfileDTO> findBestAvailableCounselor(Long slotId, LocalDate date, Gender gender, String reason);

    CounselorProfileDTO findBestAvailableCounselorForNonAcademicWithLowestDemandInMonth(Gender gender, String reason);

    CounselorProfileDTO findBestAvailableCounselorForAcademicWithLowestDemand(Gender gender, Long studentId, String reason);

    String getReasonMeaning(String reason, Long studentId);
}

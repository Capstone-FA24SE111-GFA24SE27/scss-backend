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

    CounselorProfileDTO findBestAvailableCounselorForNonAcademic(Long slotId, LocalDate date, Gender gender, Long expertiseId);

    List<SlotDTO> getAllCounselingSlots(LocalDate date, Long studentId);

    PaginationDTO<List<NonAcademicCounselorProfileDTO>> getNonAcademicCounselorsWithFilter(NonAcademicCounselorFilterRequestDTO filterRequest);

    PaginationDTO<List<AcademicCounselorProfileDTO>> getAcademicCounselorsWithFilter(AcademicCounselorFilterRequestDTO filterRequest);

    List<SpecializationDTO> getAllSpecialization();

    NonAcademicCounselorProfileDTO getNonAcademicCounselorById(Long id);

    AcademicCounselorProfileDTO getAcademicCounselorById(Long id);

    CounselorProfileDTO findBestAvailableCounselorForAcademic(Long slotId, LocalDate date, Gender gender, Long specializationId, Long departmentId, Long majorId);
}

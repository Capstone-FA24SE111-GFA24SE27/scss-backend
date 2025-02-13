package com.capstone2024.scss.domain.demand.service;

import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.demand.dto.CounselingDemandDTO;
import com.capstone2024.scss.application.demand.dto.ProblemTagCountResponse;
import com.capstone2024.scss.application.demand.dto.request.CounselingDemandCreateRequestDTO;
import com.capstone2024.scss.application.demand.dto.request.CounselingDemandFilterRequestDTO;
import com.capstone2024.scss.application.demand.dto.request.CounselingDemandUpdateRequestDTO;

import java.time.LocalDate;
import java.util.List;

public interface CounselingDemandService {
    CounselingDemandDTO createCounselingDemand(Long studentId, Long supportStaffId, CounselingDemandCreateRequestDTO counselingDemandDTO);

    CounselingDemandDTO getOne(Long id);

    PaginationDTO<List<CounselingDemandDTO>> filterCounselingDemandsForSupportStaff(CounselingDemandFilterRequestDTO filterRequest, Long supportStaffId);

    CounselingDemandDTO updateCounselingDemand(Long counselingDemandId, CounselingDemandUpdateRequestDTO updateRequestDTO);

    void deleteCounselingDemandIfWaiting(Long counselingDemandId);

    PaginationDTO<List<CounselingDemandDTO>> filterCounselingDemandsForCounselor(CounselingDemandFilterRequestDTO filterRequest, Long counselorId);

    CounselingDemandDTO solveCounselingDemand(Long counselingDemandId, String summarizeNote);

    List<CounselingDemandDTO> getAll(LocalDate from, LocalDate to);

    List<ProblemTagCountResponse> getProblemTagsAndCountBySemester(String semesterName);
}

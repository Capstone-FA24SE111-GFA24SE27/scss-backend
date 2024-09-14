package com.capstone2024.scss.domain.counseling_booking.services;

import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.counselor.dto.CounselorDTO;
import com.capstone2024.scss.application.counselor.dto.request.CounselorFilterRequestDTO;

import java.util.List;

public interface CounselorService {
    PaginationDTO<List<CounselorDTO>> getCounselorsWithFilter(CounselorFilterRequestDTO filterRequest);

    CounselorDTO getOneCounselor(Long counselorId);
}

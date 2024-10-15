package com.capstone2024.scss.domain.demand.service;

import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.demand.dto.ProblemCategoryResponseDTO;
import com.capstone2024.scss.application.demand.dto.request.ProblemCategoryRequestDTO;
import com.capstone2024.scss.application.demand.dto.request.ProblemTagFilterRequestDTO;
import com.capstone2024.scss.application.demand.dto.ProblemTagResponseDTO;
import com.capstone2024.scss.application.demand.dto.request.ProblemTagRequestDTO;

import java.util.List;

public interface ProblemTagService {
    PaginationDTO<List<ProblemTagResponseDTO>> filterProblemTags(ProblemTagFilterRequestDTO filterRequest);
    public ProblemTagResponseDTO createProblemTag(ProblemTagRequestDTO request);

    ProblemCategoryResponseDTO createProblemCategory(ProblemCategoryRequestDTO request);

    ProblemTagResponseDTO updateProblemTag(Long id, ProblemTagRequestDTO request);

    ProblemCategoryResponseDTO updateProblemCategory(Long id, ProblemCategoryRequestDTO request);

    List<ProblemCategoryResponseDTO> getAllProblemCategories();

    void deleteProblemTag(Long id);

    void deleteProblemCategory(Long id);
}

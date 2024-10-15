package com.capstone2024.scss.domain.common.mapper.demand;

import com.capstone2024.scss.application.demand.dto.ProblemCategoryResponseDTO;
import com.capstone2024.scss.application.demand.dto.ProblemTagResponseDTO;
import com.capstone2024.scss.domain.demand.entities.ProblemCategory;
import com.capstone2024.scss.domain.demand.entities.ProblemTag;

public class ProblemTagMapper {
    public static ProblemTagResponseDTO toProblemTagResponseDto(ProblemTag problemTag) {
        if(problemTag == null) {
            return null;
        }
        return ProblemTagResponseDTO.builder()
                .id(problemTag.getId())
                .name(problemTag.getName())
                .point(problemTag.getPoint())
                .category(toProblemCategoryResponseDto(problemTag.getCategory()))
                .build();
    }

    public static ProblemCategoryResponseDTO toProblemCategoryResponseDto(ProblemCategory problemCategory) {
        if (problemCategory == null) {
            return null;
        }

        return ProblemCategoryResponseDTO.builder()
                .id(problemCategory.getId())
                .name(problemCategory.getName())
                .build();
    }
}

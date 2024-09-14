package com.capstone2024.gym_management_system.application.counselor.dto.request;

import com.capstone2024.gym_management_system.application.account.dto.enums.SortDirection;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

@Builder
@Getter
public class CounselorFilterRequestDTO {
    private String search;
    private BigDecimal ratingFrom;
    private BigDecimal ratingTo;
    private SortDirection soreDirection;
    private String sortBy;
    private Pageable pagination;
}

package com.capstone2024.scss.application.counselor.dto.request;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import lombok.*;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NonAcademicCounselorFilterRequestDTO {
    private String search;
    private BigDecimal ratingFrom;
    private BigDecimal ratingTo;
    private LocalDate availableFrom;
    private LocalDate availableTo;
    private Long expertiseId; // Expertise ID for filtering
    private String sortBy;
    private SortDirection sortDirection;
    private Pageable pagination;
}

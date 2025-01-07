package com.capstone2024.scss.application.counselor.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackFilterDTO {
    private String keyword;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private BigDecimal ratingFrom;
    private BigDecimal ratingTo;
    private String sortBy;
    private String sortDirection;
    private Integer page;
    private Integer size;
}


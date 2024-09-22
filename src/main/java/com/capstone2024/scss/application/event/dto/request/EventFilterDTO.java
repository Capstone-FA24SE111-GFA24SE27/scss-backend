package com.capstone2024.scss.application.event.dto.request;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import lombok.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterDTO {
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Long semesterId;
    private String keyword;
    private Long categoryId;
    private Pageable pagination;
    private String sortBy;
    private SortDirection sortDirection;
}

package com.capstone2024.scss.application.demand.dto.request;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import lombok.*;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemTagFilterRequestDTO {
    private String keyword;
    private Long problemCategoryId;  // Thêm trường mới
    private String sortBy;
    private SortDirection sortDirection;
    private Pageable pagination;
}

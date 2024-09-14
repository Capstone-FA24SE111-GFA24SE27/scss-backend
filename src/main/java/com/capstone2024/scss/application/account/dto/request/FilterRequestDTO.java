package com.capstone2024.scss.application.account.dto.request;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.domain.account.enums.Status;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class FilterRequestDTO {
    private String search;
    private Status status;
    private SortDirection soreDirection;
    private String sortBy;
    private Pageable pagination;
}

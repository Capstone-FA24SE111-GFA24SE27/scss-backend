package com.capstone2024.scss.application.student.dto;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;

@Data
@Builder
public class StudentFilterRequestDTO {
    private String studentCode;
    private Long specializationId;
    private String sortBy;
    private SortDirection sortDirection;
    private String keyword;
    private Pageable pagination;
}

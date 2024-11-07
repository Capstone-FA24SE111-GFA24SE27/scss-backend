package com.capstone2024.scss.application.student.dto;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;

@Data
@Builder
public class StudentFilterRequestDTO {
    private String studentCode;
    private String sortBy;
    private SortDirection sortDirection;
    private String keyword;

    private StudentAcademicFilterDTO academicOption;
    private StudentGPAFilterDTO gpaOption;

    private boolean isIncludeBehavior;
    private StudentBehaviorFilterDTO behaviorOption;

    private Pageable pagination;
}

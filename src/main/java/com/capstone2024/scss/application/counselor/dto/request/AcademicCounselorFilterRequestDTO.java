package com.capstone2024.scss.application.counselor.dto.request;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import lombok.*;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicCounselorFilterRequestDTO {
    private String search;
    private BigDecimal ratingFrom;
    private BigDecimal ratingTo;
    private LocalDate availableFrom;
    private LocalDate availableTo;
    private Long specializationId;
    private Long majorId;
    private Long departmentId;
    private CounselorStatus status;
    private Gender gender;
    private String sortBy;
    private SortDirection sortDirection;
    private Pageable pagination;
}

package com.capstone2024.scss.application.student.dto;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.student.dto.enums.TypeOfAttendanceFilter;
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
    private Integer page;
    private Integer size;

    private StudentAcademicFilterDTO academicOption;
    private StudentGPAFilterDTO gpaOption;

    private TypeOfAttendanceFilter typeOfAttendanceFilter;
    private StudentAttendanceAsCountFilterDTO attendanceAsCountOption;
    private StudentAttendanceAsPercentFilterDTO attendanceAsPercentOption;

    private boolean isUsingPrompt;
    private StudentBehaviorFilterDTO behaviorOption;

    private Pageable pagination;
}

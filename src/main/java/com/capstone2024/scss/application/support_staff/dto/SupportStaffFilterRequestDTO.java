package com.capstone2024.scss.application.support_staff.dto;

import lombok.*;
import org.springframework.data.domain.PageRequest;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportStaffFilterRequestDTO {
    private String search;
    private PageRequest pagination;
}


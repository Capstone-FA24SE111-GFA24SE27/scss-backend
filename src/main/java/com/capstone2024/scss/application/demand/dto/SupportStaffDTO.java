package com.capstone2024.scss.application.demand.dto;

import com.capstone2024.scss.application.account.dto.ProfileDTO;
import com.capstone2024.scss.domain.support_staff.entity.SupportStaff;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportStaffDTO {
    private Long id;
    private ProfileDTO profile;
    private SupportStaff.SupportStaffStatus status;
}

package com.capstone2024.scss.domain.common.support_staff;

import com.capstone2024.scss.application.demand.dto.SupportStaffDTO;
import com.capstone2024.scss.domain.common.mapper.account.ProfileMapper;
import com.capstone2024.scss.domain.support_staff.entity.SupportStaff;

public class SupportStaffMapper {
    public static SupportStaffDTO toSupportStaffDTO(SupportStaff supportStaff) {
        if (supportStaff == null) {
            return null;
        }

        return SupportStaffDTO.builder()
                .id(supportStaff.getId())
                .profile(ProfileMapper.toProfileDTO(supportStaff)) // Assuming ProfileMapper handles profile fields
                .status(supportStaff.getStatus())
                .build();
    }
}

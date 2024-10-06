package com.capstone2024.scss.domain.common.mapper.appointment_counseling;

import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.domain.common.mapper.account.ProfileMapper;
import com.capstone2024.scss.domain.counselor.entities.Counselor;

public class CounselorProfileMapper {
    public static CounselorProfileDTO toCounselorProfileDTO(Counselor counselor) {
        if (counselor == null) {
            return null;
        }

        return CounselorProfileDTO.builder()
                .id(counselor.getId())
                .profile(ProfileMapper.toProfileDTO(counselor))
                .rating(counselor.getRating())
                .email(counselor.getAccount().getEmail())
                .expertise(ExpertiseMapper.toExpertiseDTO(counselor.getExpertise()))
                .build();
    }
}

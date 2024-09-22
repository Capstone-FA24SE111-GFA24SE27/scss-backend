package com.capstone2024.scss.domain.common.mapper.account;

import com.capstone2024.scss.application.account.dto.ProfileDTO;
import com.capstone2024.scss.domain.account.entities.Profile;

public class ProfileMapper {

    public static ProfileDTO toProfileDTO(Profile profile) {
        if (profile == null) {
            return null;
        }

        return ProfileDTO.builder()
                .id(profile.getId()) // Assuming `BaseEntity` has a getId() method
                .fullName(profile.getFullName())
                .phoneNumber(profile.getPhoneNumber())
                .dateOfBirth(profile.getDateOfBirth())
                .avatarLink(profile.getAvatarLink())
                .build();
    }
}

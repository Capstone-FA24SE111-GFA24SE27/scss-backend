package com.capstone2024.gym_management_system.domain.account.services.impl;

import com.capstone2024.gym_management_system.application.advice.exeptions.NotFoundException;
import com.capstone2024.gym_management_system.application.profile.dto.ProfileDTO;
import com.capstone2024.gym_management_system.domain.account.entities.Profile;
import com.capstone2024.gym_management_system.domain.account.services.ProfileService;
import com.capstone2024.gym_management_system.infrastructure.repositories.account.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public ProfileDTO getProfileByAccountId(Long accountId) {
        return mapToDTO(
                profileRepository
                        .findByAccountId(accountId)
                        .orElseThrow(
                                () -> new NotFoundException("Profile not found for account ID: " + accountId, HttpStatus.NOT_FOUND)
                        )
        );
    }

    private ProfileDTO mapToDTO(Profile profile) {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(profile.getId());
        profileDTO.setFullName(profile.getFullName());
        profileDTO.setPhoneNumber(profile.getPhoneNumber());
        profileDTO.setAddress(profile.getAddress());
        profileDTO.setDateOfBirth(profile.getDateOfBirth());
        return profileDTO;
    }
}


package com.capstone2024.scss.domain.account.services.impl;

import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.account.dto.ProfileDTO;
import com.capstone2024.scss.domain.account.services.ProfileService;
import com.capstone2024.scss.domain.common.mapper.account.ProfileMapper;
import com.capstone2024.scss.infrastructure.repositories.account.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public ProfileDTO getProfileByAccountId(Long accountId) {
        return ProfileMapper.toProfileDTO(
                profileRepository
                        .findByAccountId(accountId)
                        .orElseThrow(
                                () -> new NotFoundException("Profile not found for account ID: " + accountId, HttpStatus.NOT_FOUND)
                        )
        );
    }
}


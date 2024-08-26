package com.capstone2024.gym_management_system.domain.account.services;

import com.capstone2024.gym_management_system.application.profile.dto.ProfileDTO;
import com.capstone2024.gym_management_system.domain.account.entities.Profile;

import java.util.Optional;

public interface ProfileService {
    ProfileDTO getProfileByAccountId(Long accountId);
}

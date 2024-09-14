package com.capstone2024.scss.domain.account.services;

import com.capstone2024.scss.application.profile.dto.ProfileDTO;

public interface ProfileService {
    ProfileDTO getProfileByAccountId(Long accountId);
}

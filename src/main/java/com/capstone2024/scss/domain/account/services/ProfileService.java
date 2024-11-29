package com.capstone2024.scss.domain.account.services;

import com.capstone2024.scss.application.account.dto.ProfileDTO;
import com.capstone2024.scss.domain.account.entities.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    ProfileDTO getProfileByAccountId(Long accountId);

    String updateAvatar(Long profileId, MultipartFile file);

    Object getProfileByAccountIdForEachRole(Long accountId);
}

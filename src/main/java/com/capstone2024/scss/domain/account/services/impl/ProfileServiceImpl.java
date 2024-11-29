package com.capstone2024.scss.domain.account.services.impl;

import com.capstone2024.scss.application.account.dto.AcademicCounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.NonAcademicCounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.account.dto.ProfileDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.entities.Profile;
import com.capstone2024.scss.domain.account.services.ProfileService;
import com.capstone2024.scss.domain.common.mapper.account.CounselorProfileMapper;
import com.capstone2024.scss.domain.common.mapper.account.ProfileMapper;
import com.capstone2024.scss.domain.common.mapper.student.StudentMapper;
import com.capstone2024.scss.domain.counselor.entities.AcademicCounselor;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.counselor.entities.NonAcademicCounselor;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.infrastructure.configuration.cloudinary.service.CloudinaryService;
import com.capstone2024.scss.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.scss.infrastructure.repositories.account.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final CloudinaryService cloudinaryService;
    private final AccountRepository accountRepository;

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

    @Override
    public String updateAvatar(Long profileId, MultipartFile file) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        // Delete the old avatar if it exists
        if (profile.getAvatarLink() != null && isValidCloudinaryUrl(profile.getAvatarLink())) {
            String publicId = extractPublicIdFromUrl(profile.getAvatarLink());
            try {
                cloudinaryService.deleteFile(publicId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Upload the new avatar
        Map uploadResult = null;
        try {
            uploadResult = cloudinaryService.uploadFile(file, "avatars");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String newAvatarLink = (String) uploadResult.get("secure_url");

        // Update the profile with the new avatar link
        profile.setAvatarLink(newAvatarLink);
        profileRepository.save(profile);
        return newAvatarLink;
    }

    @Override
    public Object getProfileByAccountIdForEachRole(Long accountId) {
        Account principal = accountRepository.findById(accountId)
                        .orElseThrow(() -> new NotFoundException("No account match this Id"));

        log.info("Fetching profile for account ID: {}", principal.getId());
        switch (principal.getRole()) {
            case STUDENT -> {
                Student student = (Student) principal.getProfile();
                StudentProfileDTO studentProfileDTO = StudentMapper.toStudentProfileDTO(student);
                return ResponseUtil.getResponse(studentProfileDTO, HttpStatus.OK);
            }
            case NON_ACADEMIC_COUNSELOR -> {
                Counselor counselor = (Counselor) principal.getProfile();
                NonAcademicCounselorProfileDTO counselorProfileDTO = CounselorProfileMapper.toNonAcademicCounselorProfileDTO((NonAcademicCounselor) counselor);
                return ResponseUtil.getResponse(counselorProfileDTO, HttpStatus.OK);
            }
            case ACADEMIC_COUNSELOR -> {
                Counselor counselor = (Counselor) principal.getProfile();
                AcademicCounselorProfileDTO counselorProfileDTO = CounselorProfileMapper.toAcademicCounselorProfileDTO((AcademicCounselor) counselor);
                return ResponseUtil.getResponse(counselorProfileDTO, HttpStatus.OK);
            }
            default -> {
                throw new BadRequestException("Invalid Role");
            }
        }
    }

    private boolean isValidCloudinaryUrl(String url) {
        // Basic check for a Cloudinary URL pattern
        String cloudinaryUrlPattern = "^https://res\\.cloudinary\\.com/.+/image/upload/.+\\.(jpg|jpeg|png|gif)$";
        return url.matches(cloudinaryUrlPattern);
    }

    private String extractPublicIdFromUrl(String url) {
        // Extract public ID from the Cloudinary URL
        // For example: "https://res.cloudinary.com/demo/image/upload/v1234567890/sample.jpg"
        // Public ID would be "sample"
        String[] parts = url.split("/");
        return parts[parts.length - 1].split("\\.")[0];
    }
}


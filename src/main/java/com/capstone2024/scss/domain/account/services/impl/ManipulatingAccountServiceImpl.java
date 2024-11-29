package com.capstone2024.scss.domain.account.services.impl;

import com.capstone2024.scss.application.account.dto.create_account.AcademicCounselorAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.ManagerAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.NonAcademicCounselorAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.SupportStaffAccountDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.entities.Profile;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.account.enums.Status;
import com.capstone2024.scss.domain.account.services.ManipulatingAccountService;
import com.capstone2024.scss.domain.account.services.ProfileService;
import com.capstone2024.scss.domain.common.mapper.account.CounselorProfileMapper;
import com.capstone2024.scss.domain.counselor.entities.*;
import com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus;
import com.capstone2024.scss.domain.student.entities.Department;
import com.capstone2024.scss.domain.student.entities.Major;
import com.capstone2024.scss.infrastructure.repositories.DepartmentRepository;
import com.capstone2024.scss.infrastructure.repositories.MajorRepository;
import com.capstone2024.scss.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.scss.infrastructure.repositories.account.ProfileRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.ExpertiseRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ManipulatingAccountServiceImpl implements ManipulatingAccountService {

    private static final Logger logger = LoggerFactory.getLogger(ManipulatingAccountServiceImpl.class);
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final MajorRepository majorRepository;
    private final SpecializationRepository specializationRepository;
    private final ExpertiseRepository expertiseRepository;

    @Override
    public void createManagerAccount(ManagerAccountDTO managerAccountDTO) {
        String managerEmail = managerAccountDTO.getEmail();
        logger.info("Checking if manager account with email '{}' exists.", managerEmail);

        if (accountRepository.findAccountByEmail(managerEmail).isEmpty()) {
            logger.info("Manager account does not exist. Creating new manager account.");

            Account manager = Account.builder()
                    .email(managerEmail)
                    .role(Role.MANAGER)
                    .password(passwordEncoder.encode(managerAccountDTO.getPassword()))
                    .status(Status.ACTIVE)
                    .build();

            accountRepository.save(manager);

            Profile managerProfile = Profile.builder()
                    .account(manager)
                    .fullName(managerAccountDTO.getFullName())
                    .phoneNumber(managerAccountDTO.getPhoneNumber())
                    .avatarLink(null)
                    .dateOfBirth(managerAccountDTO.getDateOfBirth()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli())
                    .gender(managerAccountDTO.getGender())
                    .build();

            profileRepository.save(managerProfile);

            logger.info("Manager account created with email '{}'.", managerEmail);
        } else {
            logger.warn("Manager account with email '{}' already exists.", managerEmail);
            throw new IllegalArgumentException("Manager account with this email already exists.");
        }
    }

    @Override
    public void createSupportStaffAccount(SupportStaffAccountDTO supportStaffAccountDTO) {
        String supportStaffEmail = supportStaffAccountDTO.getEmail();
        logger.info("Checking if support staff account with email '{}' exists.", supportStaffEmail);

        if (accountRepository.findAccountByEmail(supportStaffEmail).isEmpty()) {
            logger.info("Support staff account does not exist. Creating new support staff account.");

            Account supportStaff = Account.builder()
                    .email(supportStaffEmail)
                    .role(Role.SUPPORT_STAFF)
                    .password(passwordEncoder.encode(supportStaffAccountDTO.getPassword()))
                    .status(Status.ACTIVE)
                    .build();

            accountRepository.save(supportStaff);

            Profile supportStaffProfile = Profile.builder()
                    .account(supportStaff)
                    .fullName(supportStaffAccountDTO.getFullName())
                    .phoneNumber(supportStaffAccountDTO.getPhoneNumber())
                    .avatarLink(null)
                    .dateOfBirth(supportStaffAccountDTO.getDateOfBirth()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli())
                    .gender(supportStaffAccountDTO.getGender())
                    .build();

            profileRepository.save(supportStaffProfile);

            logger.info("Support staff account created with email '{}'.", supportStaffEmail);
        } else {
            logger.warn("Support staff account with email '{}' already exists.", supportStaffEmail);
            throw new IllegalArgumentException("Support staff account with this email already exists.");
        }
    }

    @Override
    public void createAcademicCounselorAccount(AcademicCounselorAccountDTO dto) {
        // Find Department, Major, and Specialization by IDs
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
        Major major = majorRepository.findById(dto.getMajorId())
                .orElseThrow(() -> new IllegalArgumentException("Major not found"));
        Specialization specialization = specializationRepository.findById(dto.getSpecializationId())
                .orElseThrow(() -> new IllegalArgumentException("Specialization not found"));

        // Generate unique email
        String counselorEmail = dto.getEmail();
        logger.info("Checking if academic counselor account with email '{}' exists.", counselorEmail);

        // Check if account already exists
        if (accountRepository.findAccountByEmail(counselorEmail).isEmpty()) {
            logger.info("Academic counselor account does not exist. Creating new account for specialization '{}'.", specialization.getName());

            // Create and save Account
            Account counselor = Account.builder()
                    .email(counselorEmail)
                    .role(Role.ACADEMIC_COUNSELOR)
                    .status(Status.ACTIVE)
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .build();
            accountRepository.save(counselor);

            // Create and save Academic Counselor profile
            AcademicCounselor counselorProfile = AcademicCounselor.builder()
                    .account(counselor)
                    .fullName(dto.getFullName())
                    .phoneNumber(dto.getPhoneNumber())
                    .avatarLink(null)
                    .dateOfBirth(dto.getDateOfBirth()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli())
                    .rating(BigDecimal.ZERO)
                    .gender(dto.getGender())
                    .specialization(specialization)
                    .major(major)
                    .department(department)
                    .status(CounselorStatus.AVAILABLE)
                    .academicDegree("Thạc sĩ")

                    .specializedSkills(dto.getSpecializedSkills())
                    .otherSkills(dto.getOtherSkills())
                    .workHistory(dto.getWorkHistory())
                    .achievements(dto.getAchievements())

                    .qualifications(dto.getQualifications() != null ? dto.getQualifications().stream().map(CounselorProfileMapper::toQualification).toList() : new ArrayList<>())
                    .certifications(dto.getCertifications() != null ? dto.getCertifications().stream().map(CounselorProfileMapper::toCertification).toList() : new ArrayList<>())

                    .build();

            AvailableDateRange availableDateRange = createAvailableDateRangeFromTodayToTwoMonths(counselorProfile);
            counselorProfile.setAvailableDateRange(availableDateRange);

            profileRepository.save(counselorProfile);
            logger.info("Academic counselor account created with email '{}'.", counselorEmail);
        } else {
            logger.warn("Academic counselor account with email '{}' already exists.", counselorEmail);
            throw new IllegalArgumentException("Academic counselor account with this email already exists.");
        }
    }

    @Override
    public void createNonAcademicCounselorAccount(NonAcademicCounselorAccountDTO dto) {
        // Find Department, Major, and Specialization by IDs
        Expertise expertise = expertiseRepository.findById(dto.getExpertiseId())
                .orElseThrow(() -> new IllegalArgumentException("Expertise not found"));

        // Generate unique email
        String counselorEmail = dto.getEmail();
        logger.info("Checking if academic counselor account with email '{}' exists.", counselorEmail);

        // Check if account already exists
        if (accountRepository.findAccountByEmail(counselorEmail).isEmpty()) {

            // Create and save Account
            Account counselor = Account.builder()
                    .email(counselorEmail)
                    .role(Role.ACADEMIC_COUNSELOR)
                    .status(Status.ACTIVE)
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .build();
            accountRepository.save(counselor);

            // Create and save Academic Counselor profile
            NonAcademicCounselor counselorProfile = NonAcademicCounselor.builder()
                    .account(counselor)
                    .fullName(dto.getFullName())
                    .phoneNumber(dto.getPhoneNumber())
                    .avatarLink(null)
                    .dateOfBirth(dto.getDateOfBirth()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli())
                    .rating(BigDecimal.ZERO)
                    .gender(dto.getGender())
                    .expertise(expertise)
                    .industryExperience(5)
                    .status(CounselorStatus.AVAILABLE)

                    .specializedSkills(dto.getSpecializedSkills())
                    .otherSkills(dto.getOtherSkills())
                    .workHistory(dto.getWorkHistory())
                    .achievements(dto.getAchievements())

                    .qualifications(dto.getQualifications() != null ? dto.getQualifications().stream().map(CounselorProfileMapper::toQualification).toList() : new ArrayList<>())
                    .certifications(dto.getCertifications() != null ? dto.getCertifications().stream().map(CounselorProfileMapper::toCertification).toList() : new ArrayList<>())

                    .build();

            AvailableDateRange availableDateRange = createAvailableDateRangeFromTodayToTwoMonths(counselorProfile);
            counselorProfile.setAvailableDateRange(availableDateRange);

            profileRepository.save(counselorProfile);
            logger.info("Non Academic counselor account created with email '{}'.", counselorEmail);
        } else {
            logger.warn("Non Academic counselor account with email '{}' already exists.", counselorEmail);
            throw new IllegalArgumentException("Non Academic counselor account with this email already exists.");
        }
    }

    public AvailableDateRange createAvailableDateRangeFromTodayToTwoMonths(Counselor counselor) {
        LocalDate startDate = LocalDate.now();

        LocalDate endDate = startDate.plusMonths(2);

        return AvailableDateRange.builder()
                .startDate(startDate)
                .endDate(endDate)
                .counselor(counselor)
                .build();
    }
}


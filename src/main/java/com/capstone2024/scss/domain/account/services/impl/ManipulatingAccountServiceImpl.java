package com.capstone2024.scss.domain.account.services.impl;

import com.capstone2024.scss.application.account.dto.create_account.AcademicCounselorAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.ManagerAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.NonAcademicCounselorAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.SupportStaffAccountDTO;
import com.capstone2024.scss.application.advice.exeptions.ForbiddenException;
import com.capstone2024.scss.application.counselor.dto.CertificationDTO;
import com.capstone2024.scss.application.counselor.dto.QualificationDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.entities.Profile;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.account.enums.Status;
import com.capstone2024.scss.domain.account.services.ManipulatingAccountService;
import com.capstone2024.scss.domain.common.mapper.account.CounselorProfileMapper;
import com.capstone2024.scss.domain.counselor.entities.*;
import com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus;
import com.capstone2024.scss.domain.student.entities.Department;
import com.capstone2024.scss.domain.student.entities.Major;
import com.capstone2024.scss.infrastructure.repositories.DepartmentRepository;
import com.capstone2024.scss.infrastructure.repositories.MajorRepository;
import com.capstone2024.scss.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.scss.infrastructure.repositories.account.ProfileRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final CounselorRepository counselorRepository;
    private final QualificationRepository qualificationRepository;
    private final CertificationRepository certificationRepository;

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
                    .avatarLink(managerAccountDTO.getAvatarLink())
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
                    .avatarLink(supportStaffAccountDTO.getAvatarLink())
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
//        Specialization specialization = specializationRepository.findById(dto.getSpecializationId())
//                .orElseThrow(() -> new IllegalArgumentException("Specialization not found"));

        // Generate unique email
        String counselorEmail = dto.getEmail();
        logger.info("Checking if academic counselor account with email '{}' exists.", counselorEmail);

        // Check if account already exists
        if (accountRepository.findAccountByEmail(counselorEmail).isEmpty()) {
//            logger.info("Academic counselor account does not exist. Creating new account for specialization '{}'.", specialization.getName());

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
                    .avatarLink(dto.getAvatarLink())
                    .dateOfBirth(dto.getDateOfBirth()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli())
//                    .rating(BigDecimal.ZERO)
                    .gender(dto.getGender())
//                    .specialization(specialization)
                    .major(major)
                    .department(department)
                    .status(CounselorStatus.AVAILABLE)
//                    .academicDegree("Thạc sĩ")

                    .specializedSkills(dto.getSpecializedSkills())
                    .otherSkills(dto.getOtherSkills())
                    .workHistory(dto.getWorkHistory())
                    .achievements(dto.getAchievements())

                    .qualifications(dto.getQualifications() != null ? dto.getQualifications().stream().map(CounselorProfileMapper::toQualification).toList() : new ArrayList<>())
                    .certifications(dto.getCertifications() != null ? dto.getCertifications().stream().map(CounselorProfileMapper::toCertification).toList() : new ArrayList<>())

                    .build();

            counselorProfile.setQualifications(counselorProfile.getQualifications().stream().peek(qualification -> qualification.setCounselor(counselorProfile)).toList());

            counselorProfile.setCertifications(counselorProfile.getCertifications().stream().peek(certification -> certification.setCounselor(counselorProfile)).toList());

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
                    .avatarLink(dto.getAvatarLink())
                    .dateOfBirth(dto.getDateOfBirth()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli())
//                    .rating(BigDecimal.ZERO)
                    .gender(dto.getGender())
                    .expertise(expertise)
//                    .industryExperience(5)
                    .status(CounselorStatus.AVAILABLE)

                    .specializedSkills(dto.getSpecializedSkills())
                    .otherSkills(dto.getOtherSkills())
                    .workHistory(dto.getWorkHistory())
                    .achievements(dto.getAchievements())

                    .qualifications(dto.getQualifications() != null ? dto.getQualifications().stream().map(CounselorProfileMapper::toQualification).toList() : new ArrayList<>())
                    .certifications(dto.getCertifications() != null ? dto.getCertifications().stream().map(CounselorProfileMapper::toCertification).toList() : new ArrayList<>())

                    .build();

            counselorProfile.setQualifications(counselorProfile.getQualifications().stream().peek(qualification -> qualification.setCounselor(counselorProfile)).toList());

            counselorProfile.setCertifications(counselorProfile.getCertifications().stream().peek(certification -> certification.setCounselor(counselorProfile)).toList());

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


    @Override
    public void updateManagerAccount(ManagerAccountDTO managerAccountDTO) {
        logger.info("Updating manager account with email '{}'.", managerAccountDTO.getEmail());

        Account manager = accountRepository.findById(managerAccountDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Manager account not found"));

//        manager.setPassword(passwordEncoder.encode(managerAccountDTO.getPassword()));
        accountRepository.save(manager);

        Profile managerProfile = profileRepository.findByAccount(manager)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for manager account"));

        managerProfile.setFullName(managerAccountDTO.getFullName());
        managerProfile.setPhoneNumber(managerAccountDTO.getPhoneNumber());
        managerProfile.setAvatarLink(managerAccountDTO.getAvatarLink());
        managerProfile.setDateOfBirth(managerAccountDTO.getDateOfBirth()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
        managerProfile.setGender(managerAccountDTO.getGender());
        profileRepository.save(managerProfile);

        logger.info("Manager account with email '{}' has been updated.", managerAccountDTO.getEmail());
    }

    @Override
    public void updateSupportStaffAccount(SupportStaffAccountDTO supportStaffAccountDTO) {
        logger.info("Updating support staff account with email '{}'.", supportStaffAccountDTO.getEmail());

        Account supportStaff = accountRepository.findById(supportStaffAccountDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Support staff account not found"));

//        supportStaff.setPassword(passwordEncoder.encode(supportStaffAccountDTO.getPassword()));
        accountRepository.save(supportStaff);

        Profile supportStaffProfile = profileRepository.findByAccount(supportStaff)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for support staff account"));

        supportStaffProfile.setFullName(supportStaffAccountDTO.getFullName());
        supportStaffProfile.setPhoneNumber(supportStaffAccountDTO.getPhoneNumber());
        supportStaffProfile.setAvatarLink(supportStaffAccountDTO.getAvatarLink());
        supportStaffProfile.setDateOfBirth(supportStaffAccountDTO.getDateOfBirth()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
        supportStaffProfile.setGender(supportStaffAccountDTO.getGender());
        profileRepository.save(supportStaffProfile);

        logger.info("Support staff account with email '{}' has been updated.", supportStaffAccountDTO.getEmail());
    }

    @Override
    public void updateAcademicCounselorAccount(AcademicCounselorAccountDTO dto) {
        logger.info("Updating academic counselor account with email '{}'.", dto.getEmail());

        Account counselor = accountRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Academic counselor account not found"));

//        counselor.setPassword(passwordEncoder.encode(dto.getPassword()));
        accountRepository.save(counselor);

        AcademicCounselor counselorProfile = (AcademicCounselor) profileRepository.findByAccount(counselor)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for academic counselor account"));

        counselorProfile.setFullName(dto.getFullName());
        counselorProfile.setPhoneNumber(dto.getPhoneNumber());
        counselorProfile.setAvatarLink(dto.getAvatarLink());
        counselorProfile.setDateOfBirth(dto.getDateOfBirth()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
        counselorProfile.setGender(dto.getGender());
        counselorProfile.setSpecializedSkills(dto.getSpecializedSkills());
        counselorProfile.setOtherSkills(dto.getOtherSkills());
        counselorProfile.setWorkHistory(dto.getWorkHistory());
        counselorProfile.setAchievements(dto.getAchievements());
        profileRepository.save(counselorProfile);

        logger.info("Academic counselor account with email '{}' has been updated.", dto.getId());
    }

    @Override
    public void updateNonAcademicCounselorAccount(NonAcademicCounselorAccountDTO dto) {
        logger.info("Updating non-academic counselor account with email '{}'.", dto.getEmail());

        Account counselor = accountRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Non-academic counselor account not found"));

//        counselor.setPassword(passwordEncoder.encode(dto.getPassword()));
        accountRepository.save(counselor);

        NonAcademicCounselor counselorProfile = (NonAcademicCounselor) profileRepository.findByAccount(counselor)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for non-academic counselor account"));

        counselorProfile.setFullName(dto.getFullName());
        counselorProfile.setPhoneNumber(dto.getPhoneNumber());
        counselorProfile.setAvatarLink(dto.getAvatarLink());
        counselorProfile.setDateOfBirth(dto.getDateOfBirth()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
        counselorProfile.setGender(dto.getGender());
        counselorProfile.setSpecializedSkills(dto.getSpecializedSkills());
        counselorProfile.setOtherSkills(dto.getOtherSkills());
        counselorProfile.setWorkHistory(dto.getWorkHistory());
        counselorProfile.setAchievements(dto.getAchievements());
        profileRepository.save(counselorProfile);

        logger.info("Non-academic counselor account with email '{}' has been updated.", dto.getEmail());
    }

    @Override
    public void addQualification(Long counselorId, QualificationDTO qualificationDTO) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new ResourceNotFoundException("Counselor not found with id: " + counselorId));

        Qualification qualification = CounselorProfileMapper.toQualification(qualificationDTO);
        qualification.setCounselor(counselor);

        qualificationRepository.save(qualification);
        logger.info("Qualification added for counselor with id: {}", counselorId);
    }

    @Override
    public void addCertification(Long counselorId, CertificationDTO certificationDTO) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new ResourceNotFoundException("Counselor not found with id: " + counselorId));

        Certification certification = CounselorProfileMapper.toCertification(certificationDTO);
        certification.setCounselor(counselor);

        certificationRepository.save(certification);
        logger.info("Certification added for counselor with id: {}", counselorId);
    }

    @Override
    public void deleteQualification(Long counselorId, Long qualificationId) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new ResourceNotFoundException("Counselor not found with id: " + counselorId));

        Qualification qualification = qualificationRepository.findById(qualificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification not found with id: " + qualificationId));

        // Kiểm tra nếu Qualification thuộc về Counselor này
        if (!qualification.getCounselor().equals(counselor)) {
            throw new ForbiddenException("This qualification does not belong to the counselor.");
        }

        qualificationRepository.delete(qualification);
        logger.info("Qualification with id: {} deleted for counselor with id: {}", qualificationId, counselorId);
    }

    @Override
    public void deleteCertification(Long counselorId, Long certificationId) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new ResourceNotFoundException("Counselor not found with id: " + counselorId));

        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found with id: " + certificationId));

        // Kiểm tra nếu Certification thuộc về Counselor này
        if (!certification.getCounselor().equals(counselor)) {
            throw new ForbiddenException("This certification does not belong to the counselor.");
        }

        certificationRepository.delete(certification);
        logger.info("Certification with id: {} deleted for counselor with id: {}", certificationId, counselorId);
    }

    @Override
    public void updateQualification(Long counselorId, Long qualificationId, QualificationDTO qualificationDTO) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new ResourceNotFoundException("Counselor not found with id: " + counselorId));

        Qualification qualification = qualificationRepository.findById(qualificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification not found with id: " + qualificationId));

        // Kiểm tra nếu Qualification thuộc về Counselor này
        if (!qualification.getCounselor().equals(counselor)) {
            throw new ForbiddenException("This qualification does not belong to the counselor.");
        }

        // Cập nhật thông tin qualification từ DTO
        qualification.setDegree(qualificationDTO.getDegree());  // Bằng cấp
        qualification.setFieldOfStudy(qualificationDTO.getFieldOfStudy());  // Ngành học
        qualification.setInstitution(qualificationDTO.getInstitution());  // Cơ sở đào tạo
        qualification.setYearOfGraduation(qualificationDTO.getYearOfGraduation());  // Năm tốt nghiệp
        qualification.setImageUrl(qualificationDTO.getImageUrl());

        qualificationRepository.save(qualification);
        logger.info("Qualification with id: {} updated for counselor with id: {}", qualificationId, counselorId);
    }

    @Override
    public void updateCertification(Long counselorId, Long certificationId, CertificationDTO certificationDTO) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new ResourceNotFoundException("Counselor not found with id: " + counselorId));

        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found with id: " + certificationId));

        // Kiểm tra nếu Certification thuộc về Counselor này
        if (!certification.getCounselor().equals(counselor)) {
            throw new ForbiddenException("This certification does not belong to the counselor.");
        }

        // Cập nhật thông tin certification từ DTO
        certification.setName(certificationDTO.getName());  // Tên chứng chỉ
        certification.setOrganization(certificationDTO.getOrganization());  // Tổ chức cấp chứng chỉ
        certification.setImageUrl(certificationDTO.getImageUrl());

        certificationRepository.save(certification);
        logger.info("Certification with id: {} updated for counselor with id: {}", certificationId, counselorId);
    }

}


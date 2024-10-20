package com.capstone2024.scss.domain.common.mapper.account;

import com.capstone2024.scss.application.account.dto.AcademicCounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.NonAcademicCounselorProfileDTO;
import com.capstone2024.scss.application.counselor.dto.ExpertiseDTO;
import com.capstone2024.scss.application.counselor.dto.ManageCounselorDTO;
import com.capstone2024.scss.application.counselor.dto.SpecializationDTO;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.CounselingSlotMapper;
import com.capstone2024.scss.domain.common.mapper.counselor.AvailableDateRangeMapper;
import com.capstone2024.scss.domain.counselor.entities.*;

public class CounselorProfileMapper {
    public static CounselorProfileDTO toCounselorProfileDTO(Counselor counselor) {
        if (counselor == null) {
            return null;
        }

        return counselor.getAccount().getRole().equals(Role.ACADEMIC_COUNSELOR) ?
                CounselorProfileMapper.toAcademicCounselorProfileDTO((AcademicCounselor) counselor)
                :
                CounselorProfileMapper.toNonAcademicCounselorProfileDTO((NonAcademicCounselor) counselor);
    }

    // Method to convert Academic Counselor to DTO
    public static AcademicCounselorProfileDTO toAcademicCounselorProfileDTO(AcademicCounselor academicCounselor) {
        if (academicCounselor == null) {
            return null;
        }

        return AcademicCounselorProfileDTO.builder()
                .specialization(toSpecializationDTO(academicCounselor.getSpecialization()))
                .academicDegree(academicCounselor.getAcademicDegree())
                .id(academicCounselor.getId())
                .profile(ProfileMapper.toProfileDTO(academicCounselor))
                .rating(academicCounselor.getRating())
                .email(academicCounselor.getAccount().getEmail())
                .gender(academicCounselor.getGender())
                .status(academicCounselor.getStatus())
                .build();
    }

    // Method to convert Non-Academic Counselor to DTO
    public static NonAcademicCounselorProfileDTO toNonAcademicCounselorProfileDTO(NonAcademicCounselor nonAcademicCounselor) {
        if (nonAcademicCounselor == null) {
            return null;
        }

        return NonAcademicCounselorProfileDTO.builder()
                .expertise(toExpertiseDTO(nonAcademicCounselor.getExpertise()))
                .industryExperience(nonAcademicCounselor.getIndustryExperience())
                .id(nonAcademicCounselor.getId())
                .profile(ProfileMapper.toProfileDTO(nonAcademicCounselor))
                .rating(nonAcademicCounselor.getRating())
                .email(nonAcademicCounselor.getAccount().getEmail())
                .gender(nonAcademicCounselor.getGender())
                .status(nonAcademicCounselor.getStatus())
                .build();
    }

    public static ManageCounselorDTO toManageCounselorDTO(Counselor counselor) {
        if (counselor == null) {
            return null;
        }

        return ManageCounselorDTO.builder()
                .availableDateRange(AvailableDateRangeMapper.toAvailableDateRangeDTO(counselor.getAvailableDateRange()))
                .counselingSlot(counselor.getCounselingSlots().stream().map(CounselingSlotMapper::toDTO).toList())
                .profile(counselor.getAccount().getRole().equals(Role.ACADEMIC_COUNSELOR) ?
                        CounselorProfileMapper.toAcademicCounselorProfileDTO((AcademicCounselor) counselor)
                        :
                        CounselorProfileMapper.toNonAcademicCounselorProfileDTO((NonAcademicCounselor) counselor))
                .build();
    }

    // Method to convert Specialization to DTO
    public static SpecializationDTO toSpecializationDTO(Specialization specialization) {
        if (specialization == null) {
            return null;
        }

        return SpecializationDTO.builder()
                .id(specialization.getId())
                .name(specialization.getName())
                .build();
    }

    // Additional method to convert Expertise to DTO (assumed)
    public static ExpertiseDTO toExpertiseDTO(Expertise expertise) {
        if (expertise == null) {
            return null;
        }

        return ExpertiseDTO.builder()
                .id(expertise.getId())
                .name(expertise.getName())
                .build();
    }
}

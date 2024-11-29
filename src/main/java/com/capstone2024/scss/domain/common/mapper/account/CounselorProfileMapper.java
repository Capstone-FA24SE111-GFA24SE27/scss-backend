package com.capstone2024.scss.domain.common.mapper.account;

import com.capstone2024.scss.application.account.dto.AcademicCounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.NonAcademicCounselorProfileDTO;
import com.capstone2024.scss.application.counselor.dto.CertificationDTO;
import com.capstone2024.scss.application.counselor.dto.ExpertiseDTO;
import com.capstone2024.scss.application.counselor.dto.ManageCounselorDTO;
import com.capstone2024.scss.application.common.dto.SpecializationDTO;
import com.capstone2024.scss.application.counselor.dto.QualificationDTO;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.counselor.entities.*;
import com.capstone2024.scss.domain.common.mapper.appointment_counseling.CounselingSlotMapper;
import com.capstone2024.scss.domain.common.mapper.counselor.AvailableDateRangeMapper;
import com.capstone2024.scss.infrastructure.repositories.counselor.CounselorRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
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
                .department(AcademicDepartmentDetailMapper.toDepartmentDTO(academicCounselor.getDepartment()))
                .major(AcademicDepartmentDetailMapper.toMajorDTO(academicCounselor.getMajor()))

                .specializedSkills(academicCounselor.getSpecializedSkills())
                .otherSkills(academicCounselor.getOtherSkills())
                .workHistory(academicCounselor.getWorkHistory())
                .achievements(academicCounselor.getAchievements())

                .qualifications(academicCounselor.getQualifications().stream().map(CounselorProfileMapper::toQualificationDTO).toList())
                .certifications(academicCounselor.getCertifications().stream().map(CounselorProfileMapper::toCertificationDTO).toList())

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

                .specializedSkills(nonAcademicCounselor.getSpecializedSkills())
                .otherSkills(nonAcademicCounselor.getOtherSkills())
                .workHistory(nonAcademicCounselor.getWorkHistory())
                .achievements(nonAcademicCounselor.getAchievements())

                .qualifications(nonAcademicCounselor.getQualifications().stream().map(CounselorProfileMapper::toQualificationDTO).toList())
                .certifications(nonAcademicCounselor.getCertifications().stream().map(CounselorProfileMapper::toCertificationDTO).toList())

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

    public static CertificationDTO toCertificationDTO(Certification certification) {
        if (certification == null) {
            return null;
        }

        return CertificationDTO.builder()
                .id(certification.getId())  // Lấy ID từ entity
                .name(certification.getName())  // Tên chứng chỉ
                .organization(certification.getOrganization())  // Tổ chức cấp chứng chỉ
                .imageUrl(certification.getImageUrl())  // Đường dẫn ảnh
                .build();
    }

    public static QualificationDTO toQualificationDTO(Qualification qualification) {
        if (qualification == null) {
            return null;
        }

        return QualificationDTO.builder()
                .id(qualification.getId())  // Lấy ID từ entity
                .degree(qualification.getDegree())  // Lấy thông tin bằng cấp
                .fieldOfStudy(qualification.getFieldOfStudy())  // Lấy ngành học
                .institution(qualification.getInstitution())  // Lấy thông tin trường học
                .yearOfGraduation(qualification.getYearOfGraduation())  // Lấy năm tốt nghiệp
                .imageUrl(qualification.getImageUrl())  // Lấy đường dẫn ảnh
                .build();
    }

    public static Certification toCertification(CertificationDTO certificationDTO) {
        if (certificationDTO == null) {
            return null;
        }

        return Certification.builder()
                .id(certificationDTO.getId())  // Lấy ID từ DTO
                .name(certificationDTO.getName())  // Tên chứng chỉ
                .organization(certificationDTO.getOrganization())  // Tổ chức cấp chứng chỉ
                .imageUrl(certificationDTO.getImageUrl())  // Đường dẫn ảnh
                .build();
    }

    public static Qualification toQualification(QualificationDTO qualificationDTO) {
        if (qualificationDTO == null) {
            return null;
        }

        return Qualification.builder()
                .id(qualificationDTO.getId())  // Lấy ID từ DTO
                .degree(qualificationDTO.getDegree())  // Bằng cấp
                .fieldOfStudy(qualificationDTO.getFieldOfStudy())  // Ngành học
                .institution(qualificationDTO.getInstitution())  // Trường học
                .yearOfGraduation(qualificationDTO.getYearOfGraduation())  // Năm tốt nghiệp
                .imageUrl(qualificationDTO.getImageUrl())  // Đường dẫn ảnh
                .build();
    }
}

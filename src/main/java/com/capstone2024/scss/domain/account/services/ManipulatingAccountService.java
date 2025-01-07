package com.capstone2024.scss.domain.account.services;

import com.capstone2024.scss.application.account.dto.create_account.AcademicCounselorAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.ManagerAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.NonAcademicCounselorAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.SupportStaffAccountDTO;
import com.capstone2024.scss.application.counselor.dto.CertificationDTO;
import com.capstone2024.scss.application.counselor.dto.QualificationDTO;

public interface ManipulatingAccountService {
    void createManagerAccount(ManagerAccountDTO managerAccountDTO);

    void createSupportStaffAccount(SupportStaffAccountDTO supportStaffAccountDTO);

    void createAcademicCounselorAccount(AcademicCounselorAccountDTO dto);

    void createNonAcademicCounselorAccount(NonAcademicCounselorAccountDTO dto);

    void updateManagerAccount(ManagerAccountDTO managerAccountDTO);

    void updateSupportStaffAccount(SupportStaffAccountDTO supportStaffAccountDTO);

    void updateAcademicCounselorAccount(AcademicCounselorAccountDTO dto);

    void updateNonAcademicCounselorAccount(NonAcademicCounselorAccountDTO dto);

    void addQualification(Long counselorId, QualificationDTO qualificationDTO);

    void addCertification(Long counselorId, CertificationDTO certificationDTO);

    void deleteQualification(Long counselorId, Long qualificationId);

    void deleteCertification(Long counselorId, Long certificationId);

    void updateQualification(Long counselorId, Long qualificationId, QualificationDTO qualificationDTO);

    void updateCertification(Long counselorId, Long certificationId, CertificationDTO certificationDTO);
}

package com.capstone2024.scss.domain.account.services;

import com.capstone2024.scss.application.account.dto.create_account.AcademicCounselorAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.ManagerAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.NonAcademicCounselorAccountDTO;
import com.capstone2024.scss.application.account.dto.create_account.SupportStaffAccountDTO;

public interface ManipulatingAccountService {
    void createManagerAccount(ManagerAccountDTO managerAccountDTO);

    void createSupportStaffAccount(SupportStaffAccountDTO supportStaffAccountDTO);

    void createAcademicCounselorAccount(AcademicCounselorAccountDTO dto);

    void createNonAcademicCounselorAccount(NonAcademicCounselorAccountDTO dto);
}

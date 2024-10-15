package com.capstone2024.scss.domain.common.mapper.account;

import com.capstone2024.scss.application.authentication.dto.AccountDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.common.mapper.student.StudentMapper;
import com.capstone2024.scss.domain.counselor.entities.AcademicCounselor;
import com.capstone2024.scss.domain.counselor.entities.NonAcademicCounselor;
import com.capstone2024.scss.domain.student.entities.Student;

public class AccountMapper {

    public static AccountDTO toAccountDTO(Account account) {
        if (account == null) {
            return null;
        }

        return AccountDTO.builder()
                .id(account.getId()) // Assuming `BaseEntity` has a getId() method
                .email(account.getEmail())
                .role(account.getRole())
                .status(account.getStatus())
                .profile(ProfileMapper.toProfileDTO(account.getProfile())) // Using ProfileMapper to map Profile to ProfileDTO
                .build();
    }

    public static AccountDTO toStudentAccountDTO(Account account) {
        if (account == null || !(account.getProfile() instanceof Student)) {
            return null;
        }

        return AccountDTO.builder()
                .id(account.getId()) // Assuming `BaseEntity` has a getId() method
                .email(account.getEmail())
                .role(account.getRole())
                .status(account.getStatus())
                .profile(StudentMapper.toStudentProfileDTO((Student) account.getProfile())) // Using ProfileMapper to map Profile to ProfileDTO
                .build();
    }

    public static AccountDTO toNonAcademicCounselorAccountDTO(Account account) {
        if (account == null || !(account.getProfile() instanceof NonAcademicCounselor)) {
            return null;
        }

        return AccountDTO.builder()
//                .loginType(LoginTy)
                .id(account.getId()) // Assuming `BaseEntity` has a getId() method
                .email(account.getEmail())
                .role(account.getRole())
                .status(account.getStatus())
                .profile(CounselorProfileMapper.toNonAcademicCounselorProfileDTO((NonAcademicCounselor) account.getProfile())) // Using ProfileMapper to map Profile to ProfileDTO
                .build();
    }

    public static AccountDTO toAcademicCounselorAccountDTO(Account account) {
        if (account == null || !(account.getProfile() instanceof AcademicCounselor)) {
            return null;
        }

        return AccountDTO.builder()
//                .loginType(LoginTy)
                .id(account.getId()) // Assuming `BaseEntity` has a getId() method
                .email(account.getEmail())
                .role(account.getRole())
                .status(account.getStatus())
                .profile(CounselorProfileMapper.toAcademicCounselorProfileDTO((AcademicCounselor) account.getProfile())) // Using ProfileMapper to map Profile to ProfileDTO
                .build();
    }
}

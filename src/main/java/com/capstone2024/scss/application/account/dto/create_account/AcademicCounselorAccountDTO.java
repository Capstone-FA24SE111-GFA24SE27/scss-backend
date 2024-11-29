package com.capstone2024.scss.application.account.dto.create_account;

import com.capstone2024.scss.application.counselor.dto.CertificationDTO;
import com.capstone2024.scss.application.counselor.dto.QualificationDTO;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AcademicCounselorAccountDTO {

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotEmpty(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotEmpty(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Major ID is required")
    private Long majorId;

    @NotNull(message = "Specialization ID is required")
    private Long specializationId;

    private String specializedSkills;

    private String otherSkills;

    private String workHistory;

    private String achievements;

    private List<QualificationDTO> qualifications;

    private List<CertificationDTO> certifications;
}


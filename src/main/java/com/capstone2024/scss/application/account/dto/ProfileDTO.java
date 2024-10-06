package com.capstone2024.scss.application.account.dto;

import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private Long dateOfBirth;
    private String avatarLink;
    private Gender gender;
}

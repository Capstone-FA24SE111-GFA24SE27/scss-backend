package com.capstone2024.scss.application.account.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDTO {
    private String fullName;
    private String phoneNumber;
    private Long dateOfBirth;
    private String avatarLink;
    private ProfileDTO profile;

    private String studentCode;
}

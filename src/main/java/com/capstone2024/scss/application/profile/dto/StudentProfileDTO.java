package com.capstone2024.scss.application.profile.dto;

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

    private String studentCode;
}

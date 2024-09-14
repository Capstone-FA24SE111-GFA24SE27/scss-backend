package com.capstone2024.scss.application.profile.dto;

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
}

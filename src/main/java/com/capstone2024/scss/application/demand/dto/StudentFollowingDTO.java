package com.capstone2024.scss.application.demand.dto;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentFollowingDTO {
    private StudentProfileDTO student; // Embed the entire StudentDTO
    private String followNote;
    private LocalDateTime followDate;
}


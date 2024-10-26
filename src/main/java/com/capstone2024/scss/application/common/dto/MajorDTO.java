package com.capstone2024.scss.application.common.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MajorDTO {
    private Long id;
    private String name;
    private String code;
    private Long departmentId; // Tham chiếu đến department
}

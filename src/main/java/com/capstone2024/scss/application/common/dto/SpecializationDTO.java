package com.capstone2024.scss.application.common.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecializationDTO {
    private Long id;
    private String name;
    private Long majorId;
    private String code;
}

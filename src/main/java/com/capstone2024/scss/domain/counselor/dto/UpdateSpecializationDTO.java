package com.capstone2024.scss.domain.counselor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSpecializationDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "Major ID is required")
    private Long majorId;

    @NotNull(message = "Department ID is required")
    private Long departmentId;
}

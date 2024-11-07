package com.capstone2024.scss.infrastructure.data.fap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MajorFapResponseDTO {
    private String name;
    private String code;
    private List<SpecializationFapResponseDTO> specializations;
}


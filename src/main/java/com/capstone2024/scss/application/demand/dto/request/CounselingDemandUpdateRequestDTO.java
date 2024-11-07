package com.capstone2024.scss.application.demand.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounselingDemandUpdateRequestDTO {
    private Long counselorId;
    private String summarizeNote;
    private String contactNote;
}

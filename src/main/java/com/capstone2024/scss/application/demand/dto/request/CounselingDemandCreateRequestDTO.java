package com.capstone2024.scss.application.demand.dto.request;

import com.capstone2024.scss.domain.demand.entities.CounselingDemand;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CounselingDemandCreateRequestDTO {
    private Long counselorId;
    @NotNull
    private CounselingDemand.PriorityLevel priorityLevel;
    private String additionalInformation;
    private String issueDescription;
    private String causeDescription;
    private String contactNote;
    private CounselingDemand.DemandType demandType;
}


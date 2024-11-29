package com.capstone2024.scss.application.demand.dto.request;

import com.capstone2024.scss.domain.demand.entities.CounselingDemand;
import jakarta.validation.constraints.NotNull;
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
    private String summarizeNote;
    private String contactNote;

    @NotNull
    private CounselingDemand.PriorityLevel priorityLevel;
    private String additionalInformation;
    private String issueDescription;
    private String causeDescription;
}

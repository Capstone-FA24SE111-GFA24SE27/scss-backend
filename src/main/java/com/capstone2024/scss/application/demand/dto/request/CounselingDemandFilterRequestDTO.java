package com.capstone2024.scss.application.demand.dto.request;

import com.capstone2024.scss.domain.demand.entities.CounselingDemand;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;

@Getter
@Setter
@Builder
public class CounselingDemandFilterRequestDTO {
    private String keyword;
    private CounselingDemand.Status status;
    private PageRequest pageRequest;
}


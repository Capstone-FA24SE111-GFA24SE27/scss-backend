package com.capstone2024.scss.application.student.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentBehaviorFilterDTO {
    private Long semesterId;
    private String prompt;
    private List<String> behaviorList;
}

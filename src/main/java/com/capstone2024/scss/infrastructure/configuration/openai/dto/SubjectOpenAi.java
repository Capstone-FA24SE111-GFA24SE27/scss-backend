package com.capstone2024.scss.infrastructure.configuration.openai.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
public class SubjectOpenAi {
    private String name;
    private String grade; // Use "-" if grade is not available
    private int totalSlots;
    private int slotsPresent;
    private int slotsAbsent;
    private int remainingSlots;
    private Map<String, Integer> behaviorTags; // Behavior tag and its frequency
}

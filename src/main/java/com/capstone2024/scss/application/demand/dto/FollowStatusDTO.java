package com.capstone2024.scss.application.demand.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowStatusDTO {
    private boolean isFollowed; // Indicates if the student is followed
    private StudentFollowingDTO studentFollowingDTO; // Details of the following relationship (null if not followed)
    private boolean isYour; // Indicates if the current staff is following the student
    private SupportStaffDTO supportStaffDTO; // Details of the staff following the student
}


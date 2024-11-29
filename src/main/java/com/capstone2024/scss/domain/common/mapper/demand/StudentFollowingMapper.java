package com.capstone2024.scss.domain.common.mapper.demand;

import com.capstone2024.scss.application.demand.dto.StudentFollowingDTO;
import com.capstone2024.scss.domain.common.mapper.student.StudentMapper;
import com.capstone2024.scss.domain.demand.entities.StudentFollowing;

public class StudentFollowingMapper {
    public static StudentFollowingDTO toDTO(StudentFollowing following) {
        if(following == null) {
            return null;
        }
        return StudentFollowingDTO.builder()
                .student(StudentMapper.toStudentProfileDTO(following.getStudent())) // Map the student to StudentDTO
                .followNote(following.getFollowNote())
                .followDate(following.getFollowDate())
                .build();
    }
}

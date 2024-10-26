package com.capstone2024.scss.infrastructure.repositories.student;

import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.entities.StudentCounselingProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CounselingProfileRepository extends JpaRepository<StudentCounselingProfile, Long> {
    Optional<StudentCounselingProfile> findByStudent(Student student);
}

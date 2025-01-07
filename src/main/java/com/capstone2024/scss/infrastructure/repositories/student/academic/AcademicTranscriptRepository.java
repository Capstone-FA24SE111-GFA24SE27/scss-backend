package com.capstone2024.scss.infrastructure.repositories.student.academic;

import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.entities.academic.AcademicTranscript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AcademicTranscriptRepository extends JpaRepository<AcademicTranscript, Long> {
    List<AcademicTranscript> findByStudent(Student student);
}

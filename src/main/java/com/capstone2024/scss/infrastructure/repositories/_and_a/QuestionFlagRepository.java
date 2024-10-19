package com.capstone2024.scss.infrastructure.repositories._and_a;

import com.capstone2024.scss.domain.q_and_a.entities.QuestionFlag;
import com.capstone2024.scss.domain.student.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionFlagRepository extends JpaRepository<QuestionFlag, Long> {
    List<QuestionFlag> findByStudentAndQuestionBanIsNull(Student student);
}
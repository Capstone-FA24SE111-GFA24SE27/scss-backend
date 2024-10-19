package com.capstone2024.scss.infrastructure.repositories._and_a;

import com.capstone2024.scss.domain.q_and_a.entities.QuestionBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface QuestionBanRepository extends JpaRepository<QuestionBan, Long> {
    Optional<QuestionBan> findByStudentId(Long studentId);

    Optional<QuestionBan> findByStudentIdAndBanEndDateAfter(Long studentId, LocalDateTime currentDate);
}

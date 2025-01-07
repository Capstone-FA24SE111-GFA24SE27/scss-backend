package com.capstone2024.scss.infrastructure.repositories.counselor;

import com.capstone2024.scss.domain.counselor.entities.NonAcademicCounselor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NonAcademicCounselorRepository extends JpaRepository<NonAcademicCounselor, Long> {

    @Query("SELECT nac FROM NonAcademicCounselor nac " +
            "LEFT JOIN nac.questionCards q " +
            "WHERE (:expertiseId IS NULL OR nac.expertise.id = :expertiseId) " +
            "AND (q IS NULL OR FUNCTION('MONTH', q.createdDate) = FUNCTION('MONTH', CURRENT_DATE) " +
            "AND FUNCTION('YEAR', q.createdDate) = FUNCTION('YEAR', CURRENT_DATE)) " +
            "GROUP BY nac " +
            "ORDER BY COUNT(q) ASC")
    List<NonAcademicCounselor> findNonAcademicCounselorWithLeastQuestions(
            @Param("expertiseId") Long expertiseId);
}

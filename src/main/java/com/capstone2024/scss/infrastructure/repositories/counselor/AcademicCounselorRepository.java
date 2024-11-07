package com.capstone2024.scss.infrastructure.repositories.counselor;

import com.capstone2024.scss.domain.counselor.entities.AcademicCounselor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicCounselorRepository extends JpaRepository<AcademicCounselor, Long> {

    @Query("SELECT ac FROM AcademicCounselor ac " +
            "LEFT JOIN ac.questionCards q " +
            "WHERE (:departmentId IS NULL OR ac.department.id = :departmentId) " +
            "AND (:majorId IS NULL OR ac.major.id = :majorId) " +
            "AND (:specializationId IS NULL OR ac.specialization.id = :specializationId) " +
            "GROUP BY ac " +
            "ORDER BY COUNT(q) ASC")
    List<AcademicCounselor> findAcademicCounselorWithLeastQuestions(
            @Param("departmentId") Long departmentId,
            @Param("majorId") Long majorId,
            @Param("specializationId") Long specializationId);
}

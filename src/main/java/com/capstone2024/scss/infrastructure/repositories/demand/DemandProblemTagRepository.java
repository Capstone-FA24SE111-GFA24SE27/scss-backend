package com.capstone2024.scss.infrastructure.repositories.demand;

import com.capstone2024.scss.domain.demand.entities.DemandProblemTag;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DemandProblemTagRepository extends JpaRepository<DemandProblemTag, Long> {
    @Query("SELECT d FROM DemandProblemTag d " +
            "WHERE d.student.id = :studentId " +
            "AND (:semesterId IS NULL OR d.semester.id = :semesterId)")
    List<DemandProblemTag> findByStudentIdAndSemesterId(
            @Param("studentId") Long studentId,
            @Param("semesterId") Long semesterId
    );

    @Query("SELECT dpt FROM DemandProblemTag dpt WHERE dpt.student.id = :studentId AND dpt.isExcluded = :isExcluded")
    List<DemandProblemTag> findByStudentIdAndIsExcludedFalse(@Param("studentId") Long studentId, @Param("isExcluded") boolean isExcluded);

    @Modifying
    @Transactional
    @Query("UPDATE DemandProblemTag dpt SET dpt.isExcluded = true WHERE dpt.isExcluded = false")
    int updateIsExcludedForAllTags(boolean isExcluded);
}

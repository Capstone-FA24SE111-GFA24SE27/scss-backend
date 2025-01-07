package com.capstone2024.scss.infrastructure.repositories.demand;

import com.capstone2024.scss.domain.demand.entities.CounselingDemand;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CounselingDemandRepository extends JpaRepository<CounselingDemand, Long> {
    @Query("SELECT cd FROM CounselingDemand cd WHERE " +
            "(:keyword IS NULL OR cd.contactNote LIKE %:keyword% OR cd.summarizeNote LIKE %:keyword%) AND " +
            "(:status IS NULL OR cd.status = :status) AND " +
            "(:supportStaffId IS NULL OR cd.supportStaff.id = :supportStaffId)")
    Page<CounselingDemand> findCounselingDemandsWithFilterForSupportStaff(@Param("keyword") String keyword,
                                                           @Param("status") CounselingDemand.Status status,
                                                           @Param("supportStaffId") Long supportStaffId,
                                                           Pageable pageable);

    @Query("SELECT cd FROM CounselingDemand cd WHERE " +
            "(:keyword IS NULL OR cd.contactNote LIKE %:keyword% OR cd.summarizeNote LIKE %:keyword%) AND " +
            "(:status IS NULL OR cd.status = :status) AND " +
            "(:counselorId IS NULL OR cd.counselor.id = :counselorId)")
    Page<CounselingDemand> findCounselingDemandsWithFilterForCounselor(@Param("keyword") String keyword,
                                                                       @Param("status") CounselingDemand.Status status,
                                                                       @Param("counselorId") Long counselorId,
                                                                       Pageable pageable);

    @Query("SELECT d FROM CounselingDemand d " +
            "WHERE (:from IS NULL OR d.startDateTime >= :from) " +
            "AND (:to IS NULL OR d.startDateTime <= :to)")
    List<CounselingDemand> findAllByStartDateTimeBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("SELECT pt.name, COUNT(dpt.id) " +
            "FROM DemandProblemTag dpt " +
            "JOIN dpt.problemTag pt " +
            "JOIN dpt.semester s " +
            "WHERE s.name = :semesterName " +
            "GROUP BY pt.name " +
            "ORDER BY COUNT(dpt.id) DESC")
    List<Object[]> findProblemTagsWithCountBySemester(@Param("semesterName") String semesterName);
}

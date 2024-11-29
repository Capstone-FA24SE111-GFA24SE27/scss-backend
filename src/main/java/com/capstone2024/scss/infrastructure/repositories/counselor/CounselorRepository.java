package com.capstone2024.scss.infrastructure.repositories.counselor;

import com.capstone2024.scss.domain.counselor.entities.*;
import com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CounselorRepository extends JpaRepository<Counselor, Long> {

    @Query(value = "SELECT c FROM Counselor c " +
            "JOIN c.account a " +
            "WHERE (:keyword IS NULL OR " +
            "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:ratingFrom IS NULL OR :ratingTo IS NULL OR (c.rating BETWEEN :ratingFrom AND :ratingTo))")
    Page<Counselor> findByKeywordAndRatingRange(@Param("keyword") String keyword,
                                                @Param("ratingFrom") BigDecimal ratingFrom,
                                                @Param("ratingTo") BigDecimal ratingTo,
                                                Pageable pageable);

    @Query("SELECT DISTINCT c FROM NonAcademicCounselor c " +
            "JOIN c.counselingSlots cs " +
            "WHERE (:gender IS NULL OR c.gender = :gender) " +
            "  AND cs.startTime <= :startTime " +
            "  AND cs.endTime >= :endTime " +
            "  AND c.availableDateRange.startDate <= :date " +
            "  AND c.availableDateRange.endDate >= :date " +
            "  AND c.status = com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus.AVAILABLE " +
            "  AND (:expertise IS NULL OR c.expertise = :expertise) " +
            "  AND NOT EXISTS (" +
            "    SELECT 1 FROM CounselingAppointmentRequest cr " +
            "    WHERE cr.counselor = c " +
            "      AND cr.startTime = :startTime " +
            "      AND cr.endTime = :endTime " +
            "      AND cr.requireDate = :date " +
            "      AND (cr.status = 'WAITING' OR cr.status = 'APPROVED')" +
            ") " +
            "ORDER BY (" +
            "    SELECT COUNT(ca) FROM CounselingAppointment ca " +
            "    JOIN ca.appointmentRequest cr " +
            "    WHERE cr.counselor = c " +
            "      AND (cr.status <> 'CANCELED' AND cr.status <> 'EXPIRED' )" +
            ") ASC, " +
            "c.rating DESC")
    List<NonAcademicCounselor> findAvailableCounselorsByGenderAndExpertiseOrderedForNonAcademic(
            @Param("gender") Gender gender,
            @Param("expertise") Expertise expertise,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            Pageable pageable);

    @Query("SELECT c FROM Counselor c " +
            "WHERE EXISTS (SELECT cs FROM CounselingSlot cs " +
            "WHERE NOT EXISTS (SELECT ar FROM CounselingAppointmentRequest ar " +
            "WHERE ar.counselor = c " +
            "AND ar.requireDate = :date " +
            "AND ar.startTime = cs.startTime " +
            "AND (ar.status = 'WAITING' OR ar.status = 'APPROVED')))")
    List<Counselor> findAvailableCounselors(@Param("date") LocalDate date);

//    @Query("SELECT c FROM Counselor c " +
//            "JOIN c.account a " +
//            "WHERE (:keyword IS NULL OR " +
//            "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
//            "AND (:ratingFrom IS NULL OR :ratingTo IS NULL OR c.rating BETWEEN :ratingFrom AND :ratingTo)")
//    Page<Counselor> findByKeywordAndRatingRange(@Param("keyword") String keyword,
//                                                @Param("ratingFrom") BigDecimal ratingFrom,
//                                                @Param("ratingTo") BigDecimal ratingTo,
//                                                Pageable pageable);

    @Query("SELECT c FROM NonAcademicCounselor c " +
            "JOIN c.expertise e " +
            "WHERE (:search IS NULL OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:ratingFrom IS NULL OR c.rating >= :ratingFrom) " +
            "AND (:ratingTo IS NULL OR c.rating <= :ratingTo) " +
            "AND (:expertiseId IS NULL OR e.id = :expertiseId) " + // Filter by expertise ID
            "AND c.status = com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus.AVAILABLE " +
            "AND (:gender IS NULL OR c.gender = :gender) " +
            "AND (:availableTo IS NULL OR :availableFrom IS NULL OR c.id IN (SELECT ad.counselor.id FROM AvailableDateRange ad WHERE ad.startDate <= :availableTo AND ad.endDate >= :availableFrom))")
    Page<NonAcademicCounselor> findNonAcademicCounselorsWithFilter(
            @Param("search") String search,
            @Param("ratingFrom") BigDecimal ratingFrom,
            @Param("ratingTo") BigDecimal ratingTo,
            @Param("availableFrom") LocalDate availableFrom,
            @Param("availableTo") LocalDate availableTo,
            @Param("expertiseId") Long expertiseId,
            @Param("gender") Gender gender,
            Pageable pageable);

    @Query("SELECT c FROM NonAcademicCounselor c " +
            "JOIN c.expertise e " +
            "WHERE (:search IS NULL OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:ratingFrom IS NULL OR c.rating >= :ratingFrom) " +
            "AND (:ratingTo IS NULL OR c.rating <= :ratingTo) " +
            "AND (:expertiseId IS NULL OR e.id = :expertiseId) " + // Filter by expertise ID
            "AND (:status IS NULL OR c.status = :status) " +
            "AND (:gender IS NULL OR c.gender = :gender) " +
            "AND (:availableTo IS NULL OR :availableFrom IS NULL OR c.id IN (SELECT ad.counselor.id FROM AvailableDateRange ad WHERE ad.startDate <= :availableTo AND ad.endDate >= :availableFrom))")
    Page<NonAcademicCounselor> findNonAcademicCounselorsWithFilterForManaging(
            @Param("search") String search,
            @Param("ratingFrom") BigDecimal ratingFrom,
            @Param("ratingTo") BigDecimal ratingTo,
            @Param("availableFrom") LocalDate availableFrom,
            @Param("availableTo") LocalDate availableTo,
            @Param("expertiseId") Long expertiseId,
            @Param("status") CounselorStatus status,
            @Param("gender") Gender gender,
            Pageable pageable);

    @Query("SELECT c FROM AcademicCounselor c " +
//            "JOIN c.specialization s " +
            "JOIN c.department d " +
            "JOIN c.major m " +
            "WHERE (:search IS NULL OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:ratingFrom IS NULL OR c.rating >= :ratingFrom) " +
            "AND (:ratingTo IS NULL OR c.rating <= :ratingTo) " +
//            "AND (:specializationId IS NULL OR s.id = :specializationId) " +
            "AND (:departmentId IS NULL OR d.id = :departmentId) " +
            "AND (:majorId IS NULL OR m.id = :majorId) " +
            "AND c.status = com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus.AVAILABLE " +
            "AND (:gender IS NULL OR c.gender = :gender) " +
            "AND (:availableTo IS NULL OR :availableFrom IS NULL OR c.id IN (SELECT ad.counselor.id FROM AvailableDateRange ad WHERE ad.startDate <= :availableTo AND ad.endDate >= :availableFrom))")
    Page<AcademicCounselor> findAcademicCounselorsWithFilter(
            @Param("search") String search,
            @Param("ratingFrom") BigDecimal ratingFrom,
            @Param("ratingTo") BigDecimal ratingTo,
            @Param("availableFrom") LocalDate availableFrom,
            @Param("availableTo") LocalDate availableTo,
//            @Param("specializationId") Long specializationId,
            @Param("departmentId") Long departmentId,
            @Param("majorId") Long majorId,
            @Param("gender") Gender gender,
            Pageable pageable);

    @Query("SELECT c FROM AcademicCounselor c " +
            "JOIN c.specialization s " +
            "JOIN c.department d " +
            "JOIN c.major m " +
            "WHERE (:search IS NULL OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:ratingFrom IS NULL OR c.rating >= :ratingFrom) " +
            "AND (:ratingTo IS NULL OR c.rating <= :ratingTo) " +
            "AND (:specializationId IS NULL OR s.id = :specializationId) " +
            "AND (:departmentId IS NULL OR d.id = :departmentId) " +
            "AND (:majorId IS NULL OR m.id = :majorId) " +
            "AND (:status IS NULL OR c.status = :status) " +
            "AND (:gender IS NULL OR c.gender = :gender) " +
            "AND (:availableTo IS NULL OR :availableFrom IS NULL OR c.id IN (SELECT ad.counselor.id FROM AvailableDateRange ad WHERE ad.startDate <= :availableTo AND ad.endDate >= :availableFrom))")
    Page<AcademicCounselor> findAcademicCounselorsWithFilterForManaging(
            @Param("search") String search,
            @Param("ratingFrom") BigDecimal ratingFrom,
            @Param("ratingTo") BigDecimal ratingTo,
            @Param("availableFrom") LocalDate availableFrom,
            @Param("availableTo") LocalDate availableTo,
            @Param("specializationId") Long specializationId,
            @Param("departmentId") Long departmentId,
            @Param("majorId") Long majorId,
            @Param("status") CounselorStatus status,
            @Param("gender") Gender gender,
            Pageable pageable);

    @Query("SELECT DISTINCT c FROM AcademicCounselor c " +
            "JOIN c.counselingSlots cs " +
            "JOIN c.department d " +
            "JOIN c.major m " +
            "WHERE (:gender IS NULL OR c.gender = :gender) " +
            "  AND cs.startTime <= :startTime " +
            "  AND cs.endTime >= :endTime " +
            "  AND c.availableDateRange.startDate <= :date " +
            "  AND c.availableDateRange.endDate >= :date " +
            "  AND c.status = com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus.AVAILABLE " +
            "  AND (:specialization IS NULL OR c.specialization = :specialization) " +
            "  AND (:departmentId IS NULL OR d.id = :departmentId) " +
            "  AND (:majorId IS NULL OR m.id = :majorId) " +
            "  AND NOT EXISTS (" +
            "    SELECT 1 FROM CounselingAppointmentRequest cr " +
            "    WHERE cr.counselor = c " +
            "      AND cr.startTime = :startTime " +
            "      AND cr.endTime = :endTime " +
            "      AND cr.requireDate = :date " +
            "      AND (cr.status = 'WAITING' OR cr.status = 'APPROVED')" +
            ") " +
            "ORDER BY (" +
            "    SELECT COUNT(ca) FROM CounselingAppointment ca " +
            "    JOIN ca.appointmentRequest cr " +
            "    WHERE cr.counselor = c " +
            "      AND (cr.status <> 'CANCELED' AND cr.status <> 'EXPIRED' )" +
            ") ASC, " +
            "c.rating DESC")
    List<AcademicCounselor> findAvailableCounselorsByGenderAndExpertiseOrderedForAcademic(@Param("gender") Gender gender,
                                                                                          @Param("specialization") Specialization specialization,
                                                                                          @Param("departmentId") Long departmentId,
                                                                                          @Param("majorId") Long majorId,
                                                                                          @Param("date") LocalDate date,
                                                                                          @Param("startTime") LocalTime startTime,
                                                                                          @Param("endTime") LocalTime endTime,
                                                                                          Pageable pageable);

    @Query("SELECT DISTINCT c FROM AcademicCounselor c " +
            "JOIN c.department d " +
            "JOIN c.major m " +
            "LEFT JOIN CounselingDemand cd ON c.id = cd.counselor.id " +
            "WHERE (:gender IS NULL OR c.gender = :gender) " +
//            "  AND c.availableDateRange.startDate <= :date " +
//            "  AND c.availableDateRange.endDate >= :date " +
            "  AND c.status = com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus.AVAILABLE " +
            "  AND (:specialization IS NULL OR c.specialization = :specialization) " +
            "  AND (:departmentId IS NULL OR d.id = :departmentId) " +
            "  AND (:majorId IS NULL OR m.id = :majorId) " +
            "AND (cd.startDateTime IS NULL OR " +
            "MONTH(cd.startDateTime) = MONTH(CURRENT_DATE) AND YEAR(cd.startDateTime) = YEAR(CURRENT_DATE)) " +
            "GROUP BY c.id " +
            "ORDER BY COUNT(cd.id) ASC")
    List<AcademicCounselor> findBestAvailableCounselorForAcademicWithLowestDemand(@Param("gender") Gender gender,
                                                                                  @Param("specialization") Specialization specialization,
                                                                                  @Param("departmentId") Long departmentId,
                                                                                  @Param("majorId") Long majorId,
                                                                                  Pageable pageable);

    @Query("SELECT DISTINCT c FROM NonAcademicCounselor c " +
            "LEFT JOIN CounselingDemand cd ON c.id = cd.counselor.id " +
            "WHERE (:gender IS NULL OR c.gender = :gender) " +
//            "  AND c.availableDateRange.startDate <= :date " +
//            "  AND c.availableDateRange.endDate >= :date " +
            "  AND c.status = com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus.AVAILABLE " +
            "  AND (:expertise IS NULL OR c.expertise = :expertise) " +
            "AND (cd.startDateTime IS NULL OR " +
            "MONTH(cd.startDateTime) = MONTH(CURRENT_DATE) AND YEAR(cd.startDateTime) = YEAR(CURRENT_DATE)) " +
            "GROUP BY c.id " +
            "ORDER BY COUNT(cd.id) ASC")
    List<NonAcademicCounselor> findBestAvailableCounselorForNonAcademicWithLowestDemandInMonth(
            @Param("gender") Gender gender,
            @Param("expertise") Expertise expertise,
            Pageable pageable);
}

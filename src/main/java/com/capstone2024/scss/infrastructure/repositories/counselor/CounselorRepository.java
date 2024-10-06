package com.capstone2024.scss.infrastructure.repositories.counselor;

import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.counselor.entities.Expertise;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CounselorRepository extends JpaRepository<Counselor, Long> {

    @Query(value = "SELECT c.*, p.* FROM counselor c " +
            "JOIN profile p ON c.profile_id = p.id " +
            "JOIN account a ON p.account_id = a.id " +
            "WHERE (:keyword IS NULL OR " +
            "LOWER(p.full_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.phone_number) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:ratingFrom IS NULL OR :ratingTo IS NULL OR c.rating BETWEEN :ratingFrom AND :ratingTo)",
            countQuery = "SELECT COUNT(c.profile_id) FROM counselor c " +
                    "JOIN profile p ON c.profile_id = p.id " +
                    "JOIN account a ON p.account_id = a.id " +
                    "WHERE (:keyword IS NULL OR " +
                    "LOWER(p.full_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(p.phone_number) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                    "AND (:ratingFrom IS NULL OR :ratingTo IS NULL OR c.rating BETWEEN :ratingFrom AND :ratingTo)",
            nativeQuery = true)
    Page<Counselor> findByKeywordAndRatingRange(@Param("keyword") String keyword,
                                                @Param("ratingFrom") BigDecimal ratingFrom,
                                                @Param("ratingTo") BigDecimal ratingTo,
                                                Pageable pageable);

    @Query("SELECT DISTINCT c FROM Counselor c " +
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
            "      AND cr.status = 'WAITING'" +
            ") ASC, " +
            "c.rating DESC")
    List<Counselor> findAvailableCounselorsByGenderAndExpertiseOrdered(
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
}

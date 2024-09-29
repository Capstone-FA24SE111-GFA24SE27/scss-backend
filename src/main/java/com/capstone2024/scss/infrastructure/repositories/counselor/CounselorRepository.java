package com.capstone2024.scss.infrastructure.repositories.counselor;

import com.capstone2024.scss.domain.counselor.entities.Counselor;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

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

package com.capstone2024.scss.infrastructure.repositories._and_a;

import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.AppointmentFeedback;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionCardFeedback;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface QuestionCardFeedbackRepository extends JpaRepository<QuestionCardFeedback, Long> {
    @Query("SELECT af FROM QuestionCardFeedback af " +
            "WHERE af.counselor.id = :counselorId " +
            "AND (:keyword IS NULL OR af.comment LIKE %:keyword%) " +
            "AND (:fromDateTime IS NULL OR af.createdDate >= :fromDateTime) " +
            "AND (:toDateTime IS NULL OR af.createdDate <= :toDateTime) " +
            "AND (:ratingFrom IS NULL OR af.rating >= :ratingFrom) " +
            "AND (:ratingTo IS NULL OR af.rating <= :ratingTo)")
    Page<QuestionCardFeedback> findFeedbackForCounselorWithFilter(
            @Param("keyword") String keyword,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime,
            @Param("ratingFrom") BigDecimal ratingFrom,
            @Param("ratingTo") BigDecimal ratingTo,
            @Param("counselorId") Long counselorId,
            Pageable pageable);
}


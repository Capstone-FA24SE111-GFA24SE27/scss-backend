package com.capstone2024.scss.infrastructure.repositories._and_a;

import com.capstone2024.scss.domain.q_and_a.entities.QuestionCard;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionCardStatus;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionCardRepository extends JpaRepository<QuestionCard, Long> {
    @Query("SELECT q FROM QuestionCard q " +
            "LEFT JOIN FETCH q.counselor " +
            "LEFT JOIN q.chatSession cs " +
            "WHERE (:keyword IS NULL OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND q.student.id = :studentId " +
            "AND (:status IS NULL OR q.status = :status) " +
            "AND (:questionType IS NULL OR q.questionType = :questionType) " +
            "AND (:isTaken IS NULL OR q.isTaken = :isTaken) " +
            "AND (:isClosed IS NULL OR q.isClosed = :isClosed) " +
            "AND (:isChatSessionClosed IS NULL OR cs.isClosed = :isChatSessionClosed)")
    Page<QuestionCard> findQuestionCardsWithFilterForStudent(
            @Param("studentId") Long studentId,
            @Param("keyword") String keyword,
            @Param("status") QuestionCardStatus status,
            @Param("isTaken") Boolean isTaken,
            @Param("isClosed") Boolean isClosed,
            @Param("isChatSessionClosed") Boolean isChatSessionClosed,
            @Param("questionType") QuestionType questionType,
            Pageable pageable);

    @Query("SELECT q FROM QuestionCard q " +
            "LEFT JOIN q.chatSession cs " +
            "WHERE (:keyword IS NULL OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:studentCode IS NULL OR q.student.studentCode = :studentCode) " +
            "AND q.counselor.id = :counselorId " +
            "AND (:questionType IS NULL OR q.questionType = :questionType) " +
            "AND (:isTaken IS NULL OR q.isTaken = :isTaken) " +
            "AND (:isClosed IS NULL OR q.isClosed = :isClosed) " +
            "AND (:isChatSessionClosed IS NULL OR cs.isClosed = :isChatSessionClosed)")
    Page<QuestionCard> findQuestionCardsWithFilterForCounselor(
            @Param("studentCode") String studentCode,
            @Param("counselorId") Long counselorId,
            @Param("keyword") String keyword,
            @Param("questionType") QuestionType questionType,
            @Param("isTaken") Boolean isTaken,
            @Param("isClosed") Boolean isClosed,
            @Param("isChatSessionClosed") Boolean isChatSessionClosed,
            Pageable pageable);

    @Query("SELECT q FROM QuestionCard q " +
            "LEFT JOIN q.chatSession cs " +
            "WHERE (:keyword IS NULL OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:studentCode IS NULL OR q.student.studentCode = :studentCode) " +
            "AND q.status = com.capstone2024.scss.domain.q_and_a.enums.QuestionCardStatus.VERIFIED " +
            "AND (:questionType IS NULL OR q.questionType = :questionType) " +
            "AND (:isTaken IS NULL OR q.isTaken = :isTaken)" +
            "AND (:isClosed IS NULL OR q.isClosed = :isClosed)")
    Page<QuestionCard> findQuestionCardsLibraryForCounselor(
            @Param("studentCode") String studentCode,
            @Param("keyword") String keyword,
            @Param("questionType") QuestionType questionType,
            @Param("isTaken") Boolean isTaken,
            @Param("isTaken") Boolean isClosed,
            Pageable pageable);

    @Query("SELECT qc FROM QuestionCard qc LEFT JOIN FETCH qc.counselor WHERE qc.id = :id")
    Optional<QuestionCard> findByIdWithCounselor(@Param("id") Long id);

    @Query("SELECT q FROM QuestionCard q " +
            "LEFT JOIN q.chatSession cs " +
            "WHERE (:keyword IS NULL OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:studentCode IS NULL OR q.student.studentCode = :studentCode) " +
            "AND (:questionType IS NULL OR q.questionType = :questionType) " +
            "AND q.status = com.capstone2024.scss.domain.q_and_a.enums.QuestionCardStatus.PENDING")
    Page<QuestionCard> findQuestionCardsforSupportStaff(
            @Param("studentCode") String studentCode,
            @Param("keyword") String keyword,
            @Param("questionType") QuestionType questionType,
            Pageable pageable);
}

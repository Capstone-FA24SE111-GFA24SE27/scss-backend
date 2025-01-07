package com.capstone2024.scss.infrastructure.repositories.contribution_question_card;


import com.capstone2024.scss.domain.contribution_question_card.entities.ContributionQuestionCard;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContributionQuestionCardRepository extends JpaRepository<ContributionQuestionCard, Long> {
    @Query("SELECT cqc FROM ContributionQuestionCard cqc " +
            "WHERE (:status IS NULL OR cqc.publicStatus = :status) " +
            "AND (:counselorId IS NULL OR cqc.counselor.id = :counselorId) " +
            "AND (:categoryId IS NULL OR cqc.category.id = :categoryId) " +
            "AND (:keyword IS NULL OR LOWER(cqc.question) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(cqc.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ContributionQuestionCard> findByFiltersWithKeyword(
            @Param("status") ContributionQuestionCard.PublicStatus status,
            @Param("counselorId") Long counselorId,
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}

package com.capstone2024.scss.infrastructure.repositories.contribution_question_card;

import com.capstone2024.scss.domain.contribution_question_card.entities.ContributedQuestionCardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContributedQuestionCardCategoryRepository extends JpaRepository<ContributedQuestionCardCategory, Long> {
    ContributedQuestionCardCategory findByName(String name);
}

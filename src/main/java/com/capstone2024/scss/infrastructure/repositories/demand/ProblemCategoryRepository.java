package com.capstone2024.scss.infrastructure.repositories.demand;

import com.capstone2024.scss.domain.demand.entities.ProblemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProblemCategoryRepository extends JpaRepository<ProblemCategory, Long> {
    Optional<ProblemCategory> findByName(String name);
}

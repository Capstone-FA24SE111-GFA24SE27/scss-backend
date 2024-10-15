package com.capstone2024.scss.infrastructure.repositories.demand;

import com.capstone2024.scss.domain.demand.entities.ProblemTag;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemTagRepository extends JpaRepository<ProblemTag, Long> {

    @Query("SELECT pt FROM ProblemTag pt WHERE (:keyword IS NULL OR pt.name LIKE %:keyword%) "
            + "AND (:problemCategoryId IS NULL OR pt.category.id = :problemCategoryId)")
    Page<ProblemTag> findProblemTags(@Param("keyword") String keyword,
                                     @Param("problemCategoryId") Long problemCategoryId,
                                     Pageable pageable);
}

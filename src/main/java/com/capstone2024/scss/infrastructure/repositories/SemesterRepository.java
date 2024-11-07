package com.capstone2024.scss.infrastructure.repositories;

import com.capstone2024.scss.domain.common.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    Optional<Semester> findByName(String fall2024);
}

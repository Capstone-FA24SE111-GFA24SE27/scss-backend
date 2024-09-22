package com.capstone2024.scss.infrastructure.repositories.event;

import com.capstone2024.scss.domain.event.entities.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {}

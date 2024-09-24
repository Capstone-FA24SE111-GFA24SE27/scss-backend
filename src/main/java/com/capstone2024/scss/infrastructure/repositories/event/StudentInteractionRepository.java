package com.capstone2024.scss.infrastructure.repositories.event;

import com.capstone2024.scss.domain.event.entities.StudentInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentInteractionRepository extends JpaRepository<StudentInteraction, Long> {
}

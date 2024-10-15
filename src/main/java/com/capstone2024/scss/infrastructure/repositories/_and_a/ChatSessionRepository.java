package com.capstone2024.scss.infrastructure.repositories._and_a;

import com.capstone2024.scss.domain.q_and_a.entities.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
}

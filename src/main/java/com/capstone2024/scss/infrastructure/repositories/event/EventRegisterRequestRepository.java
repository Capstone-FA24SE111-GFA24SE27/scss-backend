package com.capstone2024.scss.infrastructure.repositories.event;

import com.capstone2024.scss.domain.event_register.entity.EventRegisterRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRegisterRequestRepository extends JpaRepository<EventRegisterRequest, Long> {
}

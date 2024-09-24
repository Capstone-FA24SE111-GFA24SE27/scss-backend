package com.capstone2024.scss.infrastructure.repositories.event;

import com.capstone2024.scss.domain.event.entities.RecapVideo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecapVideoRepository extends JpaRepository<RecapVideo, Long> {
}

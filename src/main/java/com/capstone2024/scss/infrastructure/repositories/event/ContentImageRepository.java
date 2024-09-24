package com.capstone2024.scss.infrastructure.repositories.event;

import com.capstone2024.scss.domain.event.entities.ContentImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentImageRepository extends JpaRepository<ContentImage, Long> {
}

package com.capstone2024.scss.infrastructure.repositories;

import com.capstone2024.scss.domain.counseling_booking.entities.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
}

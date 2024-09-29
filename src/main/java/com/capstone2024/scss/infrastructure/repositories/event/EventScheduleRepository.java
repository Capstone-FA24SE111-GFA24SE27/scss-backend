package com.capstone2024.scss.infrastructure.repositories.event;

import com.capstone2024.scss.domain.event.entities.EventSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventScheduleRepository extends JpaRepository<EventSchedule, Long> {
    @Query("SELECT e FROM EventSchedule e JOIN StudentEventSchedule s ON e.id = s.eventSchedule.id WHERE s.student.id = ?1 AND e.startDate <= ?2 AND e.endDate >= ?3")
    List<EventSchedule> findAllByStudentIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long studentId, LocalDateTime endDateTime, LocalDateTime startDateTime);
}

package com.capstone2024.scss.infrastructure.repositories.event;

import com.capstone2024.scss.domain.event_register.entity.StudentEventSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentEventScheduleRepository extends JpaRepository<StudentEventSchedule, Long> {

    // Find all schedules for a student where the event's schedule overlaps with the provided time frame
    @Query("SELECT ses FROM StudentEventSchedule ses WHERE ses.student.id = ?1 " +
            "AND ses.eventSchedule.startDate <= ?2 AND ses.eventSchedule.endDate >= ?3")
    List<StudentEventSchedule> findAllByStudentIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long studentId, LocalDateTime endDateTime, LocalDateTime startDateTime);

    @Query("SELECT ses FROM StudentEventSchedule ses " +
            "JOIN ses.eventSchedule es " +
            "WHERE ses.student.id = ?1 AND es.event.id = ?2")
    Optional<StudentEventSchedule> findByStudentIdAndEventId(Long studentId, Long eventId);

    @Query("SELECT ses FROM StudentEventSchedule ses " +
            "JOIN ses.eventSchedule es " +
            "WHERE ses.student.id = ?1 AND es.startDate >= ?2 AND es.endDate <= ?3")
    List<StudentEventSchedule> findAllByStudentIdAndDateRange(Long studentId, LocalDateTime from, LocalDateTime to);
}

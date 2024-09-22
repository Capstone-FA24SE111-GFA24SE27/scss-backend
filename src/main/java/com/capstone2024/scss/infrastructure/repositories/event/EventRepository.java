package com.capstone2024.scss.infrastructure.repositories.event;

import com.capstone2024.scss.domain.event.entities.Event;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e JOIN e.semester s JOIN e.category c WHERE " +
//            "(:dateFrom IS NULL OR e.startDate >= :dateFrom) AND " +
//            "(:dateTo IS NULL OR e.endDate <= :dateTo) AND " +
            "(:semesterId IS NULL OR s.id = :semesterId) AND " +
            "(:keyword IS NULL OR e.title LIKE %:keyword% OR e.content LIKE %:keyword%) AND " +
            "(:categoryId IS NULL OR c.id = :categoryId)")
    Page<Event> findEventsByFilters(
//            @Param("dateFrom") LocalDate dateFrom,
//            @Param("dateTo") LocalDate dateTo,
            @Param("semesterId") Long semesterId,
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            Pageable pageable);
}

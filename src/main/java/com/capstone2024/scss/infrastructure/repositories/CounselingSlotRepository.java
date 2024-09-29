package com.capstone2024.scss.infrastructure.repositories;

import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CounselingSlotRepository extends JpaRepository<CounselingSlot, Long> {
    @Query("SELECT s FROM CounselingSlot s")
    List<CounselingSlot> findAllSlots();

    Optional<CounselingSlot> findBySlotCode(String slotCode);
    Optional<CounselingSlot> findByStartTimeAndEndTime(LocalTime startTime, LocalTime endTime);
}

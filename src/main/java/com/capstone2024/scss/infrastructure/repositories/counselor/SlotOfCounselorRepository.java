package com.capstone2024.scss.infrastructure.repositories.counselor;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counselor.entities.SlotOfCounselor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface SlotOfCounselorRepository extends JpaRepository<SlotOfCounselor, Long> {
    List<SlotOfCounselor> findByCounselingSlotAndDayOfWeek(CounselingSlot counselingSlot, DayOfWeek dayOfWeek);
}


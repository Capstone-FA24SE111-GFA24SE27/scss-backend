package com.capstone2024.gym_management_system.infrastructure.repositories;

import com.capstone2024.gym_management_system.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public interface CounselingAppointmentRequestRepository extends JpaRepository<CounselingAppointmentRequest, Long> {
    @Query("SELECT r FROM CounselingAppointmentRequest r WHERE r.counselor.id = :counselorId AND r.requireDate BETWEEN :from AND :to")
    List<CounselingAppointmentRequest> findByCounselorIdAndRequireDateBetween(
            @Param("counselorId") Long counselorId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    List<CounselingAppointmentRequest> findByRequireDateAndStartTimeAndEndTime(
            LocalDate requireDate, LocalTime startTime, LocalTime endTime);
}

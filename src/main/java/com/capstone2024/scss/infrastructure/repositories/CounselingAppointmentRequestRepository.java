package com.capstone2024.scss.infrastructure.repositories;

import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CounselingAppointmentRequestRepository extends JpaRepository<CounselingAppointmentRequest, Long> {
    @Query("SELECT r FROM CounselingAppointmentRequest r WHERE r.counselor.id = :counselorId AND r.requireDate BETWEEN :from AND :to")
    List<CounselingAppointmentRequest> findByCounselorIdAndRequireDateBetween(
            @Param("counselorId") Long counselorId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    List<CounselingAppointmentRequest> findByRequireDateAndStartTimeAndEndTime(
            LocalDate requireDate, LocalTime startTime, LocalTime endTime);

    @Query("SELECT c FROM CounselingAppointmentRequest c " +
            "WHERE c.student.id = :studentId " +
            "AND (:dateFrom IS NULL OR c.requireDate >= :dateFrom) " +
            "AND (:dateTo IS NULL OR c.requireDate <= :dateTo) " +
            "AND (:meetingType IS NULL OR c.meetingType = :meetingType)")
    Page<CounselingAppointmentRequest> findByStudentIdAndFilters(
            @Param("studentId") Long studentId,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("meetingType") MeetingType meetingType,
            Pageable pageable
    );

    @Query("SELECT c FROM CounselingAppointmentRequest c " +
            "WHERE c.counselor.id = :counselorId " +
            "AND (:dateFrom IS NULL OR c.requireDate >= :dateFrom) " +
            "AND (:dateTo IS NULL OR c.requireDate <= :dateTo) " +
            "AND (:meetingType IS NULL OR c.meetingType = :meetingType)")
    Page<CounselingAppointmentRequest> findByCounselorIdAndFilters(
            @Param("counselorId") Long counselorId,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("meetingType") MeetingType meetingType,
            Pageable pageable
    );
}

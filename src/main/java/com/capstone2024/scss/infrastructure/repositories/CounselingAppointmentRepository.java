package com.capstone2024.scss.infrastructure.repositories;

import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CounselingAppointmentRepository extends JpaRepository<CounselingAppointment, Long> {
    @Query("SELECT a FROM CounselingAppointment a WHERE a.appointmentRequest.counselor.id = :counselorId AND a.startDateTime BETWEEN :fromDateTime AND :toDateTime")
    List<CounselingAppointment> findAllByCounselorIdAndDateRange(@Param("counselorId") Long counselorId,
                                                                 @Param("fromDateTime") LocalDateTime fromDateTime,
                                                                 @Param("toDateTime") LocalDateTime toDateTime);

    @Query("SELECT a FROM CounselingAppointment a WHERE a.appointmentRequest.student.id = :studentId AND a.startDateTime BETWEEN :fromDateTime AND :toDateTime")
    List<CounselingAppointment> findAllByStudentIdAndDateRange(@Param("studentId") Long studentId,
                                                               @Param("fromDateTime") LocalDateTime fromDateTime,
                                                               @Param("toDateTime") LocalDateTime toDateTime);
}

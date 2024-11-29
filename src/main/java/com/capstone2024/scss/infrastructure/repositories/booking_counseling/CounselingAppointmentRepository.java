package com.capstone2024.scss.infrastructure.repositories.booking_counseling;

import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.student.entities.Student;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CounselingAppointmentRepository extends JpaRepository<CounselingAppointment, Long> {
    @Query("SELECT a FROM CounselingAppointment a WHERE a.counselor.id = :counselorId AND a.startDateTime BETWEEN :fromDateTime AND :toDateTime")
    List<CounselingAppointment> findAllByCounselorIdAndDateRange(@Param("counselorId") Long counselorId,
                                                                 @Param("fromDateTime") LocalDateTime fromDateTime,
                                                                 @Param("toDateTime") LocalDateTime toDateTime);

    @Query("SELECT a FROM CounselingAppointment a WHERE a.student.id = :studentId AND a.startDateTime BETWEEN :fromDateTime AND :toDateTime")
    List<CounselingAppointment> findAllByStudentIdAndDateRange(@Param("studentId") Long studentId,
                                                               @Param("fromDateTime") LocalDateTime fromDateTime,
                                                               @Param("toDateTime") LocalDateTime toDateTime);

    @Query("SELECT a FROM CounselingAppointment a WHERE a.student.id = :studentId")
    List<CounselingAppointment> findAllByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT c FROM CounselingAppointment c WHERE c.student.id = ?1 " +
            "AND c.startDateTime <= ?2 AND c.endDateTime >= ?3")
    List<CounselingAppointment> findAllByStudentIdAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(Long studentId, LocalDateTime endDateTime, LocalDateTime startDateTime);

    @Query("SELECT ca FROM CounselingAppointment ca " +
            "WHERE (:studentCode IS NULL OR ca.student.studentCode = :studentCode) " +
            "AND (:counselor IS NULL OR ca.counselor = :counselor) " +
            "AND (:fromDate IS NULL OR ca.startDateTime >= :fromDate) " +
            "AND (:toDate IS NULL OR ca.endDateTime <= :toDate) " +
            "AND (:status IS NULL OR ca.status = :status)")
    Page<CounselingAppointment> findAppointmentsForCounselorWithFilter(
            @Param("studentCode") String studentCode,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("status") CounselingAppointmentStatus status,
            Counselor counselor,
            Pageable pageable);

    @Query("SELECT ca FROM CounselingAppointment ca WHERE " +
            "(:student IS NULL OR ca.student = :student) AND " +
            "(:fromDate IS NULL OR ca.startDateTime >= :fromDate) AND " +
            "(:toDate IS NULL OR ca.endDateTime <= :toDate) AND " +
            "(:status IS NULL OR ca.status = :status)")
    Page<CounselingAppointment> findAppointmentsForStudentWithFilter(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("status") CounselingAppointmentStatus status,
            Student student,
            Pageable pageable);

    @Query("SELECT a FROM CounselingAppointment a " +
            "WHERE (:from IS NULL OR a.startDateTime >= :from) " +
            "AND (:to IS NULL OR a.startDateTime <= :to)")
    List<CounselingAppointment> findAllByStartDateTimeBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("SELECT ca FROM CounselingAppointment ca WHERE ca.status = 'WAITING' AND ca.startDateTime >= :startOfDay AND ca.startDateTime < :endOfDay")
    List<CounselingAppointment> findWaitingAppointmentsForToday(LocalDateTime startOfDay, LocalDateTime endOfDay);
}

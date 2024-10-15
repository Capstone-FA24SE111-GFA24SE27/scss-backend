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
    @Query("SELECT a FROM CounselingAppointment a WHERE a.appointmentRequest.counselor.id = :counselorId AND a.startDateTime BETWEEN :fromDateTime AND :toDateTime")
    List<CounselingAppointment> findAllByCounselorIdAndDateRange(@Param("counselorId") Long counselorId,
                                                                 @Param("fromDateTime") LocalDateTime fromDateTime,
                                                                 @Param("toDateTime") LocalDateTime toDateTime);

    @Query("SELECT a FROM CounselingAppointment a WHERE a.appointmentRequest.student.id = :studentId AND a.startDateTime BETWEEN :fromDateTime AND :toDateTime")
    List<CounselingAppointment> findAllByStudentIdAndDateRange(@Param("studentId") Long studentId,
                                                               @Param("fromDateTime") LocalDateTime fromDateTime,
                                                               @Param("toDateTime") LocalDateTime toDateTime);

    @Query("SELECT a FROM CounselingAppointment a WHERE a.appointmentRequest.student.id = :studentId")
    List<CounselingAppointment> findAllByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT c FROM CounselingAppointment c WHERE c.appointmentRequest.student.id = ?1 " +
            "AND c.startDateTime <= ?2 AND c.endDateTime >= ?3")
    List<CounselingAppointment> findAllByStudentIdAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(Long studentId, LocalDateTime endDateTime, LocalDateTime startDateTime);

    @Query("SELECT ca FROM CounselingAppointment ca " +
            "JOIN ca.appointmentRequest car " +
            "WHERE (:studentCode IS NULL OR car.student.studentCode = :studentCode) " +
            "AND (:counselor IS NULL OR car.counselor = :counselor) " +
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

    @Query("SELECT ca FROM CounselingAppointment ca JOIN ca.appointmentRequest car WHERE " +
            "(:student IS NULL OR car.student = :student) AND " +
            "(:fromDate IS NULL OR ca.startDateTime >= :fromDate) AND " +
            "(:toDate IS NULL OR ca.endDateTime <= :toDate) AND " +
            "(:status IS NULL OR ca.status = :status)")
    Page<CounselingAppointment> findAppointmentsForStudentWithFilter(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("status") CounselingAppointmentStatus status,
            Student student,
            Pageable pageable);
}

package com.capstone2024.scss.infrastructure.repositories.student.academic;

import com.capstone2024.scss.domain.student.entities.academic.AttendanceDetail;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceDetailRepository extends JpaRepository<AttendanceDetail, Long> {
    List<AttendanceDetail> findByStudentStudy_IdAndStudentStudy_Student_StudentCode(Long attendanceId, String studentCode);

    @Query("SELECT a FROM AttendanceDetail a WHERE a.date = :date AND (a.lecturerComment IS NOT NULL AND a.lecturerComment <> '')")
    List<AttendanceDetail> findAttendanceDetailsWithComments(@Param("date") LocalDate date);
}

package com.capstone2024.scss.infrastructure.repositories.student;

import com.capstone2024.scss.domain.student.entities.Student;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT s FROM Student s " +
            "JOIN s.account a " +
            "WHERE " +
            "(:specializationId IS NULL OR s.specialization.id = :specializationId) " +
            "AND (:departmentId IS NULL OR s.department.id = :departmentId) " +
            "AND (:majorId IS NULL OR s.major.id = :majorId) " +
            "AND (:keyword IS NULL OR (LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<Student> findStudents(@Param("specializationId") Long specializationId,
                               @Param("keyword") String keyword,
                               @Param("departmentId") Long departmentId,
                               @Param("majorId") Long majorId,
                               Pageable pageable);

    @Query("SELECT s " +
            "FROM Student s " +
            "JOIN s.account a " +
            "WHERE " +
            "(:specializationId IS NULL OR s.specialization.id = :specializationId) " +
            "AND (:departmentId IS NULL OR s.department.id = :departmentId) " +
            "AND (:majorId IS NULL OR s.major.id = :majorId) " +
            "AND (:keyword IS NULL OR (LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
            "AND " +
            "(" +
            ":problemTagNames IS NULL OR (" +
            "s.id IN (" +
            "    SELECT d.student.id " +
            "    FROM DemandProblemTag d " +
            "    WHERE (:problemTagNames IS NULL OR d.problemTag.name IN :problemTagNames) " +
            "    AND (:semesterId IS NULL OR d.semester.id = :semesterId) " +
            "    GROUP BY d.student.id " +
            "    HAVING COUNT(d.problemTag.id) > 0" +
            "))" +
            ") " +
            "ORDER BY (SELECT COUNT(d2.id) " +
            "          FROM DemandProblemTag d2 " +
            "          WHERE d2.student.id = s.id " +
            "          AND (:problemTagNames IS NULL OR d2.problemTag.name IN :problemTagNames) " +
            "          AND (:semesterId IS NULL OR d2.semester.id = :semesterId)) DESC")
    Page<Student> findStudentsByProblemTagsAndOptionalSemester(
            @Param("specializationId") Long specializationId,
            @Param("keyword") String keyword,
            @Param("departmentId") Long departmentId,
            @Param("majorId") Long majorId,
            @Param("problemTagNames") List<String> problemTagNames,
            @Param("semesterId") Long semesterId,
            Pageable pageable
    );

    @Query("SELECT s " +
            "FROM Student s " +
            "JOIN s.account a " +
            "WHERE " +
            "(:specializationId IS NULL OR s.specialization.id = :specializationId) " +
            "AND (:departmentId IS NULL OR s.department.id = :departmentId) " +
            "AND (:majorId IS NULL OR s.major.id = :majorId) " +
            "AND (:keyword IS NULL OR (LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
            "AND " +
            "s.id IN (" +
            "    SELECT d.student.id " +
            "    FROM DemandProblemTag d " +
            "    WHERE d.isExcluded = false " +
            "    AND (:semesterId IS NULL OR d.semester.id = :semesterId) " +
            "    GROUP BY d.student.id " +
            "    HAVING COUNT(d.problemTag.id) > 0" +
            ")" +
            "ORDER BY (SELECT COUNT(d2.id) " +
            "          FROM DemandProblemTag d2 " +
            "          WHERE d2.student.id = s.id " +
            "          AND d2.isExcluded = false " +
            "          AND (:semesterId IS NULL OR d2.semester.id = :semesterId)) DESC")
    Page<Student> findStudentsByProblemTagsRecommend(
            @Param("specializationId") Long specializationId,
            @Param("keyword") String keyword,
            @Param("departmentId") Long departmentId,
            @Param("majorId") Long majorId,
//            @Param("problemTagNames") List<String> problemTagNames,
            @Param("semesterId") Long semesterId,
            Pageable pageable
    );

    Optional<Student> findByStudentCode(String studentCode);

    @Query("SELECT sto FROM Student sto " +
            "WHERE EXISTS (" +
            "    SELECT st1.studentCode " +
            "    FROM StudentStudy a1 " +
            "    JOIN a1.attendanceDetails ad1 " +
            "    JOIN a1.semester s1 " +
            "    JOIN a1.student st1 " +
            "    WHERE s1.name = :semesterIdForAttendance " +
            "      AND ad1.status = 'ABSENT' " +
            "      AND st1.studentCode = sto.studentCode " +
            "    GROUP BY st1.studentCode, a1.subjectCode " +
            "    HAVING COUNT(ad1.id) BETWEEN :absenceSlotFrom AND :absenceSlotTo" +
            ") " +
            "AND (" +
            "    SELECT COUNT(DISTINCT a2.subjectCode) " +
            "    FROM StudentStudy a2 " +
            "    JOIN a2.attendanceDetails ad2 " +
            "    JOIN a2.semester s2 " +
            "    JOIN a2.student st2 " +
            "    WHERE s2.name = :semesterIdForAttendance " +
            "      AND ad2.status = 'ABSENT' " +
            "      AND st2.studentCode = sto.studentCode " +
            "      AND (" +
            "          SELECT COUNT(*) " +
            "          FROM StudentStudy a3 " +
            "          JOIN a3.attendanceDetails ad3 " +
            "          JOIN a3.semester s3 " +
            "          JOIN a3.student st3 " +
            "          WHERE s3.name = :semesterIdForAttendance " +
            "            AND ad3.status = 'ABSENT' " +
            "            AND st3.studentCode = st2.studentCode " +
            "            AND a3.subjectCode = a2.subjectCode" +
            "      ) BETWEEN :absenceSlotFrom AND :absenceSlotTo" +
//            ") BETWEEN :subjectcountFrom AND :subjectcountTo")
            ") >= :subjectcountFrom")
    List<Student> findStudentsWithAbsenceCountRange(
            @Param("semesterIdForAttendance") String semesterIdForAttendance,
            @Param("absenceSlotFrom") Long absenceSlotFrom,
            @Param("absenceSlotTo") Long absenceSlotTo
            ,
            @Param("subjectcountFrom") Long subjectcountFrom
//            ,
//            @Param("subjectcountTo") Long subjectcountTo
    );

    @Query("SELECT sto FROM Student sto " +
            "WHERE EXISTS (" +
            "    SELECT st1.studentCode " +
            "    FROM StudentStudy a1 " +
            "    JOIN a1.attendanceDetails ad1 " +
            "    JOIN a1.semester s1 " +
            "    JOIN a1.student st1 " +
            "    WHERE s1.name = :semesterIdForAttendance " +
            "      AND ad1.status = 'ABSENT' " +
            "      AND st1.studentCode = sto.studentCode " +
            "    GROUP BY st1.studentCode, a1.subjectCode " +
            "    HAVING (COUNT(ad1.id) * 100.0) / " +
            "           (SELECT COUNT(adTotal.id) " +
            "            FROM AttendanceDetail adTotal " +
            "            JOIN adTotal.studentStudy aTotal " +
            "            JOIN aTotal.student stTotal " +
            "            WHERE aTotal.subjectCode = a1.subjectCode " +
            "              AND stTotal.studentCode = st1.studentCode " +
            "              AND aTotal.semester.name = :semesterIdForAttendance) " +
            "           BETWEEN :absenceSlotFrom AND :absenceSlotTo" +
            ") " +
            "AND (" +
            "    SELECT COUNT(DISTINCT a2.subjectCode) " +
            "    FROM StudentStudy a2 " +
            "    JOIN a2.attendanceDetails ad2 " +
            "    JOIN a2.semester s2 " +
            "    JOIN a2.student st2 " +
            "    WHERE s2.name = :semesterIdForAttendance " +
            "      AND ad2.status = 'ABSENT' " +
            "      AND st2.studentCode = sto.studentCode " +
            "      AND (" +
            "          SELECT (COUNT(ad3.id) * 100.0) / " +
            "                 (SELECT COUNT(adTotal.id) " +
            "                  FROM AttendanceDetail adTotal " +
            "                  JOIN adTotal.studentStudy aTotal " +
            "                  JOIN aTotal.student stTotal " +
            "                  WHERE aTotal.subjectCode = a3.subjectCode " +
            "                    AND stTotal.studentCode = st3.studentCode " +
            "                    AND aTotal.semester.name = :semesterIdForAttendance) " +
            "          FROM StudentStudy a3 " +
            "          JOIN a3.attendanceDetails ad3 " +
            "          JOIN a3.semester s3 " +
            "          JOIN a3.student st3 " +
            "          WHERE s3.name = :semesterIdForAttendance " +
            "            AND ad3.status = 'ABSENT' " +
            "            AND st3.studentCode = st2.studentCode " +
            "            AND a3.subjectCode = a2.subjectCode" +
            "      ) BETWEEN :absenceSlotFrom AND :absenceSlotTo" +
//            ") BETWEEN :subjectcountFrom AND :subjectcountTo")
            ") >= :subjectcountFrom")
    List<Student> findStudentsWithAbsencePercentageRange(
            @org.springframework.data.repository.query.Param("semesterIdForAttendance") String semesterIdForAttendance,
            @org.springframework.data.repository.query.Param("absenceSlotFrom") Double absenceSlotFrom,  // phần trăm tối thiểu
            @org.springframework.data.repository.query.Param("absenceSlotTo") Double absenceSlotTo,      // phần trăm tối đa
            @org.springframework.data.repository.query.Param("subjectcountFrom") Long subjectcountFrom
//            ,
//            @Param("subjectcountTo") Long subjectcountTo
    );

    @Query("SELECT sto FROM Student sto " +
            "WHERE (" +
            "    SELECT AVG(a1.finalGrade) " +
            "    FROM StudentStudy a1 " +
            "    JOIN a1.semester s1 " +
            "    JOIN a1.student st1 " +
            "    WHERE s1.name = :semesterIdForGPA " +
            "      AND st1.studentCode = sto.studentCode " +
            ") BETWEEN :from AND :to")
    List<Student> findStudentsWithGPA(
            @org.springframework.data.repository.query.Param("semesterIdForGPA") String semesterIdForGPA,
            @org.springframework.data.repository.query.Param("from") Double from,  // phần trăm tối thiểu
            @org.springframework.data.repository.query.Param("to") Double to
//            ,
//            @Param("subjectcountTo") Long subjectcountTo
    );
}

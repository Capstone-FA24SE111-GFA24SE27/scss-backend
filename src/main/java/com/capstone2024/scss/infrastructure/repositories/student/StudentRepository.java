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
}

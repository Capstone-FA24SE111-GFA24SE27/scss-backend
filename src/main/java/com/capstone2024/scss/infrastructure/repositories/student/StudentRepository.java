package com.capstone2024.scss.infrastructure.repositories.student;

import com.capstone2024.scss.domain.student.entities.Student;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT s FROM Student s " +
            "JOIN s.account a " +
            "WHERE (:studentCode IS NULL OR s.studentCode = :studentCode) " +
            "AND (:specializationId IS NULL OR s.specialization.id = :specializationId) " +
            "AND (:keyword IS NULL OR (LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<Student> findStudents(@Param("studentCode") String studentCode,
                               @Param("specializationId") Long specializationId,
                               @Param("keyword") String keyword,
                               Pageable pageable);

    Optional<Student> findByStudentCode(String studentCode);
}

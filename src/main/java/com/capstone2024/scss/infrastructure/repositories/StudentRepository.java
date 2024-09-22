package com.capstone2024.scss.infrastructure.repositories;

import com.capstone2024.scss.domain.student.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}

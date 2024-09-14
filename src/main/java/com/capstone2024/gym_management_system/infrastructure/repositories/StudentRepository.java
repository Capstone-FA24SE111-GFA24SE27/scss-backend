package com.capstone2024.gym_management_system.infrastructure.repositories;

import com.capstone2024.gym_management_system.domain.counseling_booking.entities.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}

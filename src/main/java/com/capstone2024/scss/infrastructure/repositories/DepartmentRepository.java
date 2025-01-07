package com.capstone2024.scss.infrastructure.repositories;

import com.capstone2024.scss.domain.student.entities.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByName(String name);

    Page<Department> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Optional<Department> findByCode(String code);
}


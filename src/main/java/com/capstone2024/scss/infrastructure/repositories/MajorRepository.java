package com.capstone2024.scss.infrastructure.repositories;

import com.capstone2024.scss.domain.student.entities.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<Major, Long> {
    Optional<Major> findByName(String name);

    List<Major> findByDepartmentId(Long departmentId);
}
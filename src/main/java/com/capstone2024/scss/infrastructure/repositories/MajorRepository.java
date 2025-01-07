package com.capstone2024.scss.infrastructure.repositories;

import aj.org.objectweb.asm.commons.Remapper;
import com.capstone2024.scss.domain.student.entities.Major;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<Major, Long> {
    Optional<Major> findByName(String name);

    List<Major> findByDepartmentId(Long departmentId);

    Page<Major> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Optional<Major> findByCode(String code);
}
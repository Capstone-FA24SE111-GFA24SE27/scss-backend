package com.capstone2024.scss.infrastructure.repositories.counselor;

import aj.org.objectweb.asm.commons.Remapper;
import com.capstone2024.scss.domain.counselor.entities.Specialization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    Optional<Specialization> findByName(String name);

    List<Specialization> findByMajorId(Long majorId);

    Page<Specialization> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Optional<Specialization> findByCode(String code);
}

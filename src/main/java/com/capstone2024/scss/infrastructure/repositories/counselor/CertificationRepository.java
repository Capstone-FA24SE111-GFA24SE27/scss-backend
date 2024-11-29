package com.capstone2024.scss.infrastructure.repositories.counselor;

import com.capstone2024.scss.domain.counselor.entities.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
}


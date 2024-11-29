package com.capstone2024.scss.infrastructure.repositories.demand;

import com.capstone2024.scss.domain.demand.entities.StudentFollowing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentFollowingRepository extends JpaRepository<StudentFollowing, Long> {
    boolean existsByStudentIdAndSupportStaffId(Long studentId, Long staffId);

    Optional<StudentFollowing> findByStudentIdAndSupportStaffId(Long studentId, Long staffId);

    Page<StudentFollowing> findAllBySupportStaffId(Long staffId, Pageable pageable);

    Optional<StudentFollowing> findByStudentId(Long studentId);
}


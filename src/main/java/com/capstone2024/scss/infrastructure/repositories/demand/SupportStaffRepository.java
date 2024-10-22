package com.capstone2024.scss.infrastructure.repositories.demand;

import com.capstone2024.scss.domain.support_staff.entity.SupportStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportStaffRepository extends JpaRepository<SupportStaff, Long> {

    // Custom query methods can be added here if needed
}

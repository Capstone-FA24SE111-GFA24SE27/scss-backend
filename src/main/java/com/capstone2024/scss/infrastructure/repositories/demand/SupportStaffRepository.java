package com.capstone2024.scss.infrastructure.repositories.demand;

import com.capstone2024.scss.domain.support_staff.entity.SupportStaff;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportStaffRepository extends JpaRepository<SupportStaff, Long> {
    @Query("SELECT ss FROM SupportStaff ss JOIN ss.account a WHERE " +
            "(:search IS NULL OR " +
            "LOWER(ss.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(ss.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(ss.address) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<SupportStaff> findByFilters(
            @Param("search") String search,
            Pageable pageable);
}

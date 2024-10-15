package com.capstone2024.scss.infrastructure.repositories.account;

import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.account.enums.Status;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findAccountByEmail(String email);

    Boolean existsAccountByEmail(String email);

    @Query("SELECT a FROM Account a JOIN a.profile p WHERE " +
            "(:search IS NULL OR a.email LIKE %:search% OR p.fullName LIKE %:search%) " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:role IS NULL OR a.role = :role)")
    Page<Account> findAccountsBySearchAndStatus(@Param("search") String search, @Param("status") Status status, @Param("role") Role role, Pageable pageable);

    Optional<Account> findByEmail(String email);
}

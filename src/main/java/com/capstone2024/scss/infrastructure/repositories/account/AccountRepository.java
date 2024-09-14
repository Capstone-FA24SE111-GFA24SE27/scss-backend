package com.capstone2024.scss.infrastructure.repositories.account;

import com.capstone2024.scss.domain.account.entities.Account;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findAccountByEmail(String email);

    Boolean existsAccountByEmail(String email);

    @Query(value = "SELECT a.* FROM account a " +
            "JOIN profile p ON a.id = p.account_id " +
            "WHERE (a.email LIKE %:search% OR p.full_name LIKE %:search%) " +
            "AND a.status LIKE %:status%",
            countQuery = "SELECT COUNT(*) FROM account a " +
                    "JOIN profile p ON a.id = p.account_id " +
                    "WHERE (a.email LIKE %:search% OR p.full_name LIKE %:search%) " +
                    "AND a.status LIKE %:status%",
            nativeQuery = true)
    Page<Account> findAccountsBySearchAndStatus(@Param("search") String search, @Param("status") String status, Pageable pageable);
}

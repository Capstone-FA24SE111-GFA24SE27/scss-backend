package com.capstone2024.gym_management_system.infrastructure.repositories.account;

import com.capstone2024.gym_management_system.domain.account.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findAccountByEmail(String email);

    Boolean existsAccountByEmail(String email);
}

package com.capstone2024.gym_management_system.infrastructure.repositories.account;

import com.capstone2024.gym_management_system.domain.account.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByAccountId(Long accountId);
}

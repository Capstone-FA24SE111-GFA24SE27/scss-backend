package com.capstone2024.gym_management_system.infrastructure.repositories.account;

import com.capstone2024.gym_management_system.domain.account.entities.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginTypeRepository extends JpaRepository<LoginType, Long> {
}

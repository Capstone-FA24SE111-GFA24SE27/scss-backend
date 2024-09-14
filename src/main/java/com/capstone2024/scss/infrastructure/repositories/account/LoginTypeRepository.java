package com.capstone2024.scss.infrastructure.repositories.account;

import com.capstone2024.scss.domain.account.entities.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginTypeRepository extends JpaRepository<LoginType, Long> {
}

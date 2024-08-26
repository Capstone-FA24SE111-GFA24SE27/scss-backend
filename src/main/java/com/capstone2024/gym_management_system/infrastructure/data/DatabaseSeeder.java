package com.capstone2024.gym_management_system.infrastructure.data;

import com.capstone2024.gym_management_system.domain.account.entities.Account;
import com.capstone2024.gym_management_system.domain.account.entities.Profile;
import com.capstone2024.gym_management_system.domain.account.enums.Role;
import com.capstone2024.gym_management_system.domain.account.enums.Status;
import com.capstone2024.gym_management_system.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.gym_management_system.infrastructure.repositories.account.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(AccountRepository accountRepository, ProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@example.com";
        logger.info("Checking if admin account with email '{}' exists.", adminEmail);

        if (accountRepository.findAccountByEmail(adminEmail).isEmpty()) {
            logger.info("Admin account does not exist. Creating new admin account.");

            Account admin = Account.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin112233")) // Encode the password
                    .role(Role.ADMIN)
                    .status(Status.ACTIVE)
                    .build();

            accountRepository.save(admin);

            // Create and save Profile for the admin account
            Profile adminProfile = Profile.builder()
                    .account(admin)
                    .fullName("Admin")
                    .phoneNumber("1234567890")
                    .address("123 Admin Street")
                    .dateOfBirth(LocalDate.of(1980, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build();

            profileRepository.save(adminProfile);

            logger.info("Admin account created with email '{}'.", adminEmail);
        } else {
            logger.warn("Admin account with email '{}' already exists.", adminEmail);
        }
    }
}

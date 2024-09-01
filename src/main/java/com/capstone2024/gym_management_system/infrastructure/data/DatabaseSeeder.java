package com.capstone2024.gym_management_system.infrastructure.data;

import com.capstone2024.gym_management_system.domain.account.entities.Account;
import com.capstone2024.gym_management_system.domain.account.entities.LoginType;
import com.capstone2024.gym_management_system.domain.account.entities.Profile;
import com.capstone2024.gym_management_system.domain.account.enums.LoginMethod;
import com.capstone2024.gym_management_system.domain.account.enums.Role;
import com.capstone2024.gym_management_system.domain.account.enums.Status;
import com.capstone2024.gym_management_system.domain.notification.entities.Notification;
import com.capstone2024.gym_management_system.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.gym_management_system.infrastructure.repositories.account.LoginTypeRepository;
import com.capstone2024.gym_management_system.infrastructure.repositories.account.ProfileRepository;
import com.capstone2024.gym_management_system.infrastructure.repositories.notification.NotificationRepository;
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
    private final LoginTypeRepository loginTypeRepository;
    private final NotificationRepository notificationRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(AccountRepository accountRepository, LoginTypeRepository loginTypeRepository, NotificationRepository notificationRepository, ProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.loginTypeRepository = loginTypeRepository;
        this.notificationRepository = notificationRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private void createAdminAccount() {
        String adminEmail = "admin@example.com";
        logger.info("Checking if admin account with email '{}' exists.", adminEmail);

        if (accountRepository.findAccountByEmail(adminEmail).isEmpty()) {
            logger.info("Admin account does not exist. Creating new admin account.");

            Account admin = Account.builder()
                    .email(adminEmail)
                    .role(Role.ADMINISTRATOR)
                    .status(Status.ACTIVE)
                    .build();

            accountRepository.save(admin);

            LoginType adminLoginType = LoginType.builder()
                    .password(passwordEncoder.encode("admin112233"))
                    .method(LoginMethod.DEFAULT)
                    .account(admin)
                    .build();

            loginTypeRepository.save(adminLoginType);

            // Create and save Profile for the admin account
            Profile adminProfile = Profile.builder()
                    .account(admin)
                    .fullName("Admin")
                    .phoneNumber("1234567890")
                    .address("123 Admin Street")
                    .dateOfBirth(LocalDate.of(1980, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build();

            profileRepository.save(adminProfile);

            createNotificationForAdmin(admin);

            logger.info("Admin account created with email '{}'.", adminEmail);
        } else {
            logger.warn("Admin account with email '{}' already exists.", adminEmail);
        }
    }

    private void createNotificationForAdmin(Account admin) {
        Notification notification = Notification.builder()
                .receiver(admin)
                .title("new noti")
                .message("content")
                .sender("SYSTEM")
                .build();
        notificationRepository.save(notification);
    }

    private void createStudentAccount() {
        String studentEmail = "student@example.com";
        logger.info("Checking if student account with email '{}' exists.", studentEmail);

        if (accountRepository.findAccountByEmail(studentEmail).isEmpty()) {
            logger.info("student account does not exist. Creating new student account.");

            Account student = Account.builder()
                    .email(studentEmail)
                    .role(Role.STUDENT)
                    .status(Status.ACTIVE)
                    .build();

            accountRepository.save(student);

            LoginType studentLoginType = LoginType.builder()
                    .password(passwordEncoder.encode("student112233"))
                    .method(LoginMethod.DEFAULT)
                    .account(student)
                    .build();

            loginTypeRepository.save(studentLoginType);

            // Create and save Profile for the student account
            Profile adminProfile = Profile.builder()
                    .account(student)
                    .fullName("Student")
                    .phoneNumber("1234567890")
                    .address("123 Admin Street")
                    .dateOfBirth(LocalDate.of(1980, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build();

            profileRepository.save(adminProfile);

            logger.info("student account created with email '{}'.", studentEmail);
        } else {
            logger.warn("student account with email '{}' already exists.", studentEmail);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        createAdminAccount();
        createStudentAccount();
    }
}

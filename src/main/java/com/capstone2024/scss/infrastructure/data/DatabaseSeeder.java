package com.capstone2024.scss.infrastructure.data;

import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.entities.LoginType;
import com.capstone2024.scss.domain.account.entities.Profile;
import com.capstone2024.scss.domain.account.enums.LoginMethod;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.account.enums.Status;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.OnlineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.CounselingAppointmentRequestStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import com.capstone2024.scss.domain.counseling_booking.entities.counselor.Counselor;
import com.capstone2024.scss.domain.counseling_booking.entities.student.Student;
import com.capstone2024.scss.domain.notification.entities.Notification;
import com.capstone2024.scss.infrastructure.repositories.*;
import com.capstone2024.scss.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.scss.infrastructure.repositories.account.LoginTypeRepository;
import com.capstone2024.scss.infrastructure.repositories.account.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final AccountRepository accountRepository;
    private final LoginTypeRepository loginTypeRepository;
    private final NotificationRepository notificationRepository;
    private final CounselorRepository counselorRepository;
    private final StudentRepository studentRepository;
    private final CounselingAppointmentRequestRepository counselingAppointmentRequestRepository;
    private final CounselingAppointmentRepository counselingAppointmentRepository;
    private final CounselingSlotRepository counselingSlotRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(AccountRepository accountRepository, LoginTypeRepository loginTypeRepository, NotificationRepository notificationRepository, CounselorRepository counselorRepository, StudentRepository studentRepository, CounselingAppointmentRequestRepository counselingAppointmentRequestRepository, CounselingAppointmentRepository counselingAppointmentRepository, CounselingSlotRepository counselingSlotRepository, ProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.loginTypeRepository = loginTypeRepository;
        this.notificationRepository = notificationRepository;
        this.counselorRepository = counselorRepository;
        this.studentRepository = studentRepository;
        this.counselingAppointmentRequestRepository = counselingAppointmentRequestRepository;
        this.counselingAppointmentRepository = counselingAppointmentRepository;
        this.counselingSlotRepository = counselingSlotRepository;
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
                    .avatarLink("https://www.strasys.uk/wp-content/uploads/2022/02/Depositphotos_484354208_S.jpg")
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
            Student studentProfile = Student.builder()
                    .account(student)
                    .fullName("Student")
                    .phoneNumber("1234567890")
                    .avatarLink("https://www.strasys.uk/wp-content/uploads/2022/02/Depositphotos_484354208_S.jpg")
                    .dateOfBirth(LocalDate.of(1980, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .studentCode("SE111111")
                    .build();

            profileRepository.save(studentProfile);

            logger.info("student account created with email '{}'.", studentEmail);
        } else {
            logger.warn("student account with email '{}' already exists.", studentEmail);
        }
    }

    private void createStudentAccount2() {
        String studentEmail = "student2@example.com";
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
            Student studentProfile = Student.builder()
                    .account(student)
                    .fullName("Student2")
                    .phoneNumber("1234567890")
                    .avatarLink("https://www.strasys.uk/wp-content/uploads/2022/02/Depositphotos_484354208_S.jpg")
                    .dateOfBirth(LocalDate.of(1980, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .studentCode("SE111112")
                    .build();

            profileRepository.save(studentProfile);

            logger.info("student account created with email '{}'.", studentEmail);
        } else {
            logger.warn("student account with email '{}' already exists.", studentEmail);
        }
    }

    private void createCounselorAccount() {
        String counselorEmail = "counselor@example.com";
        logger.info("Checking if counselor account with email '{}' exists.", counselorEmail);

        if (accountRepository.findAccountByEmail(counselorEmail).isEmpty()) {
            logger.info("counselor account does not exist. Creating new counselor account.");

            Account counselor = Account.builder()
                    .email(counselorEmail)
                    .role(Role.COUNSELOR)
                    .status(Status.ACTIVE)
                    .build();

            accountRepository.save(counselor);

            LoginType counselorLoginType = LoginType.builder()
                    .password(passwordEncoder.encode("counselor112233"))
                    .method(LoginMethod.DEFAULT)
                    .account(counselor)
                    .build();

            loginTypeRepository.save(counselorLoginType);

            // Create and save Profile for the counselor account
            Counselor counselorProfile = Counselor.builder()
                    .account(counselor)
                    .fullName("Counselor")
                    .phoneNumber("1234567890")
                    .avatarLink("https://www.strasys.uk/wp-content/uploads/2022/02/Depositphotos_484354208_S.jpg")
                    .dateOfBirth(LocalDate.of(1980, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .rating(BigDecimal.valueOf(4.6))
                    .build();

            profileRepository.save(counselorProfile);

            generateSlots();
            
            createCounselingAppointmentRequest(counselorProfile);

            logger.info("counselor account created with email '{}'.", counselorEmail);
        } else {
            logger.warn("counselor account with email '{}' already exists.", counselorEmail);
        }
    }

    private void createCounselingAppointmentRequest(Counselor counselor) {

        Student student = studentRepository.findById(2L).orElseThrow(() -> new NotFoundException("Student Not Found"));

        CounselingAppointmentRequest appointmentRequest = CounselingAppointmentRequest.builder()
                .requireDate(LocalDate.of(2024, 11, 16)) // Ngày 16/11/2024
                .startTime(LocalTime.of(8, 0)) // 08:30
                .endTime(LocalTime.of(9, 30)) // Ví dụ giờ kết thúc
                .status(CounselingAppointmentRequestStatus.APPROVED)
                .meetingType(MeetingType.ONLINE)
                .reason("Counseling session")
                .counselor(counselor)
                .student(student)
                .build();

        counselingAppointmentRequestRepository.save(appointmentRequest);

        createCounselingAppointment(appointmentRequest);
    }

    private void createCounselingAppointment(CounselingAppointmentRequest appointmentRequest) {
        CounselingAppointment appointment = CounselingAppointment.builder()
                .startDateTime(LocalDateTime.of(appointmentRequest.getRequireDate(), appointmentRequest.getStartTime()))
                .endDateTime(LocalDateTime.of(appointmentRequest.getRequireDate(), appointmentRequest.getEndTime()))
                .status(CounselingAppointmentStatus.WAITING)
                .appointmentRequest(appointmentRequest)
                .build();

        counselingAppointmentRepository.save(appointment);
    }

    public void generateSlots() {
        LocalTime startTime = LocalTime.of(8, 0); // Bắt đầu lúc 08:00 sáng
        LocalTime endTime = startTime.plusHours(1).plusMinutes(30); // Thời gian kết thúc của slot đầu tiên

        LocalTime lunchBreakStart = LocalTime.of(12, 0);
        LocalTime lunchBreakEnd = LocalTime.of(13, 0);
        for (int i = 1; i <= 5; i++) {
            // Tạo một slot với thời gian bắt đầu và kết thúc
            CounselingSlot slot = CounselingSlot.builder()
                    .slotCode("Slot-" + i)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();

            counselingSlotRepository.save(slot);

            // Cập nhật thời gian bắt đầu và kết thúc cho slot tiếp theo
            startTime = endTime.plusMinutes(15); // Thêm khoảng cách giữa các slot
            endTime = startTime.plusHours(1).plusMinutes(30); // Thêm thời gian cho slot tiếp theo

            // Điều chỉnh thời gian nếu slot chồng lên thời gian nghỉ trưa
            if (startTime.isBefore(lunchBreakEnd) && endTime.isAfter(lunchBreakStart)) {
                // Nếu slot bắt đầu trước khi nghỉ trưa và kết thúc sau khi nghỉ trưa
                if (startTime.isBefore(lunchBreakStart)) {
                    // Nếu slot bắt đầu trước thời gian nghỉ trưa
                    endTime = lunchBreakStart; // Cập nhật thời gian kết thúc để kết thúc trước thời gian nghỉ trưa
                }
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
        createAdminAccount();
        createStudentAccount();
        createStudentAccount2();
        createCounselorAccount();
    }
}

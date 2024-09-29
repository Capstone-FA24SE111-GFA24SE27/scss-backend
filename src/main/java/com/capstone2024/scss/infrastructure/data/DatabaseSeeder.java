package com.capstone2024.scss.infrastructure.data;

import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.entities.LoginType;
import com.capstone2024.scss.domain.account.entities.Profile;
import com.capstone2024.scss.domain.account.enums.LoginMethod;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.account.enums.Status;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OfflineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OnlineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.CounselingAppointmentRequestStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.event.entities.*;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.notification.entities.Notification;
import com.capstone2024.scss.infrastructure.repositories.*;
import com.capstone2024.scss.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.scss.infrastructure.repositories.account.LoginTypeRepository;
import com.capstone2024.scss.infrastructure.repositories.account.ProfileRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.CounselorRepository;
import com.capstone2024.scss.infrastructure.repositories.event.*;
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
import java.util.ArrayList;
import java.util.List;

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
    private final CategoryRepository categoryRepository;
    private final SemesterRepository semesterRepository;
    private final EventRepository eventRepository;
    private final EventScheduleRepository eventScheduleRepository;
    private final TrainingPointRepository trainingPointRepository;
    private final RecapVideoRepository recapVideoRepository;
    private final ContentImageRepository contentImageRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(AccountRepository accountRepository, LoginTypeRepository loginTypeRepository, NotificationRepository notificationRepository, CounselorRepository counselorRepository, StudentRepository studentRepository, CounselingAppointmentRequestRepository counselingAppointmentRequestRepository, CounselingAppointmentRepository counselingAppointmentRepository, CounselingSlotRepository counselingSlotRepository, ProfileRepository profileRepository, CategoryRepository categoryRepository, SemesterRepository semesterRepository, EventRepository eventRepository, EventScheduleRepository eventScheduleRepository, TrainingPointRepository trainingPointRepository, RecapVideoRepository recapVideoRepository, ContentImageRepository contentImageRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.loginTypeRepository = loginTypeRepository;
        this.notificationRepository = notificationRepository;
        this.counselorRepository = counselorRepository;
        this.studentRepository = studentRepository;
        this.counselingAppointmentRequestRepository = counselingAppointmentRequestRepository;
        this.counselingAppointmentRepository = counselingAppointmentRepository;
        this.counselingSlotRepository = counselingSlotRepository;
        this.profileRepository = profileRepository;
        this.categoryRepository = categoryRepository;
        this.semesterRepository = semesterRepository;
        this.eventRepository = eventRepository;
        this.eventScheduleRepository = eventScheduleRepository;
        this.trainingPointRepository = trainingPointRepository;
        this.recapVideoRepository = recapVideoRepository;
        this.contentImageRepository = contentImageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        createAdminAccount();
        createStudentAccount();
        createStudentAccount2();
        createCounselorAccount();

        createCategories();
        createSemesters();
    }

    private void createCategories() {
        // Tạo 4 danh mục
        for (int i = 1; i <= 4; i++) {
            Category category = new Category("CODE" + i, "Category " + i);
            categoryRepository.save(category);
        }
    }

    private void createSemesters() {
        // Tạo các học kỳ cho năm 2024 và 2025
        List<Semester> semesters = new ArrayList<>();
        String[] semesterNames = {"Summer", "Fall", "Spring"};
        LocalDate startDate;
        LocalDate endDate;

        for (int year = 2024; year <= 2025; year++) {
            for (int i = 0; i < semesterNames.length; i++) {
                switch (semesterNames[i]) {
                    case "Summer":
                        startDate = LocalDate.of(year, 1, 1);
                        endDate = LocalDate.of(year, 4, 30);
                        break;
                    case "Fall":
                        startDate = LocalDate.of(year, 5, 1);
                        endDate = LocalDate.of(year, 8, 31);
                        break;
                    case "Spring":
                        startDate = LocalDate.of(year, 9, 1);
                        endDate = LocalDate.of(year, 12, 31);
                        break;
                    default:
                        continue; // Nếu không phải là học kỳ hợp lệ thì bỏ qua
                }
                Semester semester = Semester.builder()
                        .semesterCode(semesterNames[i] + year)
                        .name(semesterNames[i] + " " + year)
                        .startDate(startDate)
                        .endDate(endDate)
                        .build();
                semesters.add(semester);
            }
        }

        semesterRepository.saveAll(semesters);

        // Tạo sự kiện và TrainingPoint cho từng học kỳ
        for (Semester semester : semesters) {
            createEventsForSemester(semester);
            createTrainingPointsForSemester(semester);
        }
    }

    private void createTrainingPointsForSemester(Semester semester) {
        // Tạo TrainingPoint cho mỗi sinh viên
        List<Student> students = studentRepository.findAll();
        for (Student student : students) {
            TrainingPoint trainingPoint = TrainingPoint.builder()
                    .student(student)
                    .point(65) // Hoặc bất kỳ điểm nào bạn muốn khởi tạo
                    .semester(semester)
                    .build();

            // Lưu TrainingPoint vào database
            trainingPointRepository.save(trainingPoint);
        }
    }

    private void createEventsForSemester(Semester semester) {
        // Lấy danh sách các danh mục
        List<Category> categories = categoryRepository.findAll();

        for (int i = 1; i <= 2; i++) { // Tạo 5 sự kiện cho mỗi học kỳ
            String eventContent = "This is the detailed content for Event " + i + " in " + semester.getName();
            Event event = Event.builder()
                    .title("Event " + i + " - " + semester.getName())
                    .content(eventContent)
                    .displayImage("https://www.mecc.nl/wp-content/uploads/2021/12/Header_zakelijk_event_IC_1440x600.jpg")
                    .view(0)
                    .isNeedAccept(false)
                    .category(categories.get(i % categories.size()))
                    .semester(semester)
                    .build();

            eventRepository.save(event);

            // Tạo lịch cho các sự kiện trong phạm vi thời gian của học kỳ
            LocalDateTime startDateTime = semester.getStartDate().atTime(10, 0) // Bắt đầu vào 10:00 của ngày bắt đầu học kỳ
                    .plusDays(i * 9); // Thay đổi ngày bắt đầu cho từng sự kiện
            LocalDateTime endDateTime = startDateTime.plusHours(2); // Kéo dài 2 tiếng

            // Đảm bảo thời gian kết thúc không vượt quá ngày kết thúc của học kỳ
            if (endDateTime.isAfter(semester.getEndDate().atTime(23, 59))) {
                endDateTime = semester.getEndDate().atTime(23, 59);
            }

            EventSchedule schedule = EventSchedule.builder()
                    .event(event)
                    .startDate(startDateTime)
                    .endDate(endDateTime)
                    .maxParticipants(10)
                    .currentParticipants(0)
                    .address("Address for Event " + i)
                    .build();

            eventScheduleRepository.save(schedule);

            // Tạo RecapVideo
            RecapVideo recapVideo = RecapVideo.builder()
                    .event(event)
                    .videoUrl("https://res.cloudinary.com/dd8y8sska/video/upload/v1727066591/video/hulrayq22xw75ofaoxrg.mp4")
                    .build();
            recapVideoRepository.save(recapVideo);

            // Tạo ContentImage
            ContentImage contentImage = ContentImage.builder()
                    .event(event)
                    .imageUrl("https://res.cloudinary.com/dd8y8sska/image/upload/v1724948516/cld-sample-5.jpg")
                    .build();
            contentImageRepository.save(contentImage);
            ContentImage contentImage2 = ContentImage.builder()
                    .event(event)
                    .imageUrl("https://res.cloudinary.com/dd8y8sska/image/upload/v1724948516/cld-sample-5.jpg")
                    .build();
            contentImageRepository.save(contentImage2);
        }
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

            createNotificationForStudent(student);

            logger.info("student account created with email '{}'.", studentEmail);
        } else {
            logger.warn("student account with email '{}' already exists.", studentEmail);
        }
    }

    private void createNotificationForStudent(Account student) {
        Notification notification = Notification.builder()
                .receiver(student)
                .title("new noti")
                .message("content")
                .sender("SYSTEM")
                .build();
        notificationRepository.save(notification);
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
            createCounselingAppointmentRequest2(counselorProfile);

            logger.info("counselor account created with email '{}'.", counselorEmail);
        } else {
            logger.warn("counselor account with email '{}' already exists.", counselorEmail);
        }
    }

    private void createCounselingAppointmentRequest(Counselor counselor) {

        Student student = studentRepository.findById(2L).orElseThrow(() -> new NotFoundException("Student Not Found"));

        CounselingAppointmentRequest appointmentRequest = CounselingAppointmentRequest.builder()
                .requireDate(LocalDate.of(2024, 9, 13)) // Ngày 16/11/2024
                .startTime(LocalTime.of(8, 0)) // 08:30
                .endTime(LocalTime.of(9, 0)) // Ví dụ giờ kết thúc
                .status(CounselingAppointmentRequestStatus.APPROVED)
                .meetingType(MeetingType.ONLINE)
                .reason("Counseling session")
                .counselor(counselor)
                .student(student)
                .build();

        counselingAppointmentRequestRepository.save(appointmentRequest);

        createOnlineCounselingAppointment(appointmentRequest);
    }

    private void createCounselingAppointmentRequest2(Counselor counselor) {

        Student student = studentRepository.findById(2L).orElseThrow(() -> new NotFoundException("Student Not Found"));

        CounselingAppointmentRequest appointmentRequest = CounselingAppointmentRequest.builder()
                .requireDate(LocalDate.of(2024, 9, 20)) // Ngày 16/11/2024
                .startTime(LocalTime.of(9, 15)) // 08:30
                .endTime(LocalTime.of(10, 15)) // Ví dụ giờ kết thúc
                .status(CounselingAppointmentRequestStatus.APPROVED)
                .meetingType(MeetingType.OFFLINE)
                .reason("Counseling session")
                .counselor(counselor)
                .student(student)
                .build();

        counselingAppointmentRequestRepository.save(appointmentRequest);

        createOfflineCounselingAppointment(appointmentRequest);
    }

    private void createOnlineCounselingAppointment(CounselingAppointmentRequest appointmentRequest) {
        OnlineAppointment appointment = OnlineAppointment.builder()
                .startDateTime(LocalDateTime.of(appointmentRequest.getRequireDate(), appointmentRequest.getStartTime()))
                .endDateTime(LocalDateTime.of(appointmentRequest.getRequireDate(), appointmentRequest.getEndTime()))
                .status(CounselingAppointmentStatus.ATTEND)
                .appointmentRequest(appointmentRequest)
                .meetUrl("hehehehe")
                .build();

        counselingAppointmentRepository.save(appointment);
    }

    private void createOfflineCounselingAppointment(CounselingAppointmentRequest appointmentRequest) {
        OfflineAppointment appointment = OfflineAppointment.builder()
                .startDateTime(LocalDateTime.of(appointmentRequest.getRequireDate(), appointmentRequest.getStartTime()))
                .endDateTime(LocalDateTime.of(appointmentRequest.getRequireDate(), appointmentRequest.getEndTime()))
                .status(CounselingAppointmentStatus.WAITING)
                .appointmentRequest(appointmentRequest)
                .address("hahaha")
                .build();

        counselingAppointmentRepository.save(appointment);
    }

    public void generateSlots() {
        LocalTime startTime = LocalTime.of(8, 0); // Bắt đầu lúc 08:00 sáng
        LocalTime endTime = startTime.plusHours(1).plusMinutes(0); // Thời gian kết thúc của slot đầu tiên

        LocalTime lunchBreakStart = LocalTime.of(12, 0);
        LocalTime lunchBreakEnd = LocalTime.of(13, 0);
        for (int i = 1; i <= 3; i++) {
            // Tạo một slot với thời gian bắt đầu và kết thúc
            CounselingSlot slot = CounselingSlot.builder()
                    .slotCode("Slot-" + i)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();

            counselingSlotRepository.save(slot);

            // Cập nhật thời gian bắt đầu và kết thúc cho slot tiếp theo
            startTime = endTime.plusMinutes(15); // Thêm khoảng cách giữa các slot
            endTime = startTime.plusHours(1).plusMinutes(0); // Thêm thời gian cho slot tiếp theo

            // Điều chỉnh thời gian nếu slot chồng lên thời gian nghỉ trưa
//            if (startTime.isBefore(lunchBreakEnd) && endTime.isAfter(lunchBreakStart)) {
//                // Nếu slot bắt đầu trước khi nghỉ trưa và kết thúc sau khi nghỉ trưa
//                if (startTime.isBefore(lunchBreakStart)) {
//                    // Nếu slot bắt đầu trước thời gian nghỉ trưa
//                    endTime = lunchBreakStart; // Cập nhật thời gian kết thúc để kết thúc trước thời gian nghỉ trưa
//                }
//            }
        }

        startTime = lunchBreakEnd;
        endTime = startTime.plusHours(1).plusMinutes(0);

        for (int i = 4; i <= 6; i++) {
            // Tạo một slot với thời gian bắt đầu và kết thúc
            CounselingSlot slot = CounselingSlot.builder()
                    .slotCode("Slot-" + i)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();

            counselingSlotRepository.save(slot);

            // Cập nhật thời gian bắt đầu và kết thúc cho slot tiếp theo
            startTime = endTime.plusMinutes(15); // Thêm khoảng cách giữa các slot
            endTime = startTime.plusHours(1).plusMinutes(0); // Thêm thời gian cho slot tiếp theo
        }
    }

}

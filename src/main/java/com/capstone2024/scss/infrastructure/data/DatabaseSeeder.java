package com.capstone2024.scss.infrastructure.data;

import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.entities.LoginType;
import com.capstone2024.scss.domain.account.entities.Profile;
import com.capstone2024.scss.domain.account.enums.LoginMethod;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.account.enums.Status;
import com.capstone2024.scss.domain.counselor.entities.*;
import com.capstone2024.scss.domain.common.utils.RandomUtil;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counseling_booking.entities.Holiday;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OfflineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OnlineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import com.capstone2024.scss.domain.demand.entities.CounselingDemand;
import com.capstone2024.scss.domain.demand.entities.DemandProblemTag;
import com.capstone2024.scss.domain.demand.entities.ProblemCategory;
import com.capstone2024.scss.domain.demand.entities.ProblemTag;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionCard;
import com.capstone2024.scss.domain.q_and_a.entities.Topic;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionCardStatus;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionType;
import com.capstone2024.scss.domain.q_and_a.enums.TopicType;
import com.capstone2024.scss.domain.student.entities.Department;
import com.capstone2024.scss.domain.student.entities.Major;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.notification.entities.Notification;
import com.capstone2024.scss.domain.student.entities.StudentCounselingProfile;
import com.capstone2024.scss.domain.student.enums.CounselingProfileStatus;
import com.capstone2024.scss.domain.support_staff.entity.SupportStaff;
import com.capstone2024.scss.infrastructure.repositories.*;
import com.capstone2024.scss.infrastructure.repositories._and_a.QuestionCardRepository;
import com.capstone2024.scss.infrastructure.repositories._and_a.TopicRepository;
import com.capstone2024.scss.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.scss.infrastructure.repositories.account.LoginTypeRepository;
import com.capstone2024.scss.infrastructure.repositories.account.ProfileRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingAppointmentRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingAppointmentRequestRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingSlotRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.AvailableDateRangeRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.CounselorRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.ExpertiseRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.SpecializationRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.CounselingDemandRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.ProblemCategoryRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.ProblemTagRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.SupportStaffRepository;
import com.capstone2024.scss.infrastructure.repositories.student.CounselingProfileRepository;
import com.capstone2024.scss.infrastructure.repositories.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
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
    private final ExpertiseRepository expertiseRepository;
    private final PasswordEncoder passwordEncoder;
    private final AvailableDateRangeRepository availableDateRangeRepository;
    private final HolidayRepository holidayRepository;
    private final SpecializationRepository specializationRepository;
    private final QuestionCardRepository questionCardRepository;
    private final ProblemTagRepository problemTagRepository;
    private final ProblemCategoryRepository problemCategoryRepository;
    private final CounselingDemandRepository counselingDemandRepository;
    private final SupportStaffRepository supportStaffRepository;
    private final TopicRepository topicRepository;
    private final CounselingProfileRepository counselingProfileRepository;
    private final DepartmentRepository departmentRepository;
    private final MajorRepository majorRepository;

    @Override
    public void run(String... args) throws Exception {
        seedTopics();
        seedProblemTags();
        createAdminAccount();
//        createStudentAccounts();
        createManagerAccount();
        createSupportStaffAccount();
//        createCounselorAccount();
        generateSlots();
        createCounselorAccounts();
        createStudentAccounts();
        createVietnamHolidays();
    }

    @Transactional
    public void seedProblemTags() {
        // Tạo danh sách các ProblemCategories
        List<ProblemCategory> categories = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            ProblemCategory category = new ProblemCategory();
            category.setName("Problem Category " + i);

            // Tạo danh sách ProblemTags cho mỗi category
            List<ProblemTag> problemTags = new ArrayList<>();
            for (int j = 1; j <= 3; j++) {
                ProblemTag tag = new ProblemTag();
                tag.setName("Problem Tag " + i + "-" + j);
                tag.setPoint(j);
                tag.setCategory(category);

                // Thêm ProblemTag vào danh sách
                problemTags.add(tag);
            }

            category.setProblemTags(problemTags);  // Thiết lập danh sách ProblemTags cho category
            categories.add(category);  // Thêm category vào danh sách
        }

        // Lưu các ProblemCategory vào cơ sở dữ liệu
        problemCategoryRepository.saveAll(categories);
    }

    private void createVietnamHolidays() {
        List<Holiday> holidays = new ArrayList<>();

        // Tết Dương Lịch (1/1)
        holidays.add(Holiday.builder()
                .startDate(LocalDate.of(LocalDate.now().getYear(), 1, 1))
                .endDate(LocalDate.of(LocalDate.now().getYear(), 1, 1))
//                .type(HolidayType.SINGLE_DAY)
                .description("Tết Dương Lịch")
                .name("Tết Dương Lịch")
                .build());

        // Tết Nguyên Đán (7 ngày từ 29 tháng chạp đến mùng 5 tháng giêng)
        holidays.add(Holiday.builder()
                .startDate(LocalDate.of(LocalDate.now().getYear(), 2, 10)) // Ví dụ: ngày 29 tháng chạp
                .endDate(LocalDate.of(LocalDate.now().getYear(), 2, 16))   // Ví dụ: mùng 5 tháng giêng
//                .type(HolidayType.MULTIPLE_DAYS)
                .description("Tết Nguyên Đán")
                .name("Ngày Giải Phóng Miền Nam")
                .build());

        // Giỗ Tổ Hùng Vương (10/3 âm lịch)
        holidays.add(Holiday.builder()
                .startDate(LocalDate.of(LocalDate.now().getYear(), 4, 19)) // Giả định ngày 10/3 âm lịch rơi vào 19/4
                .endDate(LocalDate.of(LocalDate.now().getYear(), 4, 19))
//                .type(HolidayType.SINGLE_DAY)
                .description("Giỗ Tổ Hùng Vương")
                .name("Ngày Giải Phóng Miền Nam")
                .build());

        // Ngày Giải Phóng Miền Nam (30/4)
        holidays.add(Holiday.builder()
                .startDate(LocalDate.of(LocalDate.now().getYear(), 4, 30))
                .endDate(LocalDate.of(LocalDate.now().getYear(), 4, 30))
//                .type(HolidayType.SINGLE_DAY)
                .description("Ngày Giải Phóng Miền Nam")
                .name("Ngày Giải Phóng Miền Nam")
                .build());

        // Ngày Quốc Tế Lao Động (1/5)
        holidays.add(Holiday.builder()
                .startDate(LocalDate.of(LocalDate.now().getYear(), 5, 1))
                .endDate(LocalDate.of(LocalDate.now().getYear(), 5, 1))
//                .type(HolidayType.SINGLE_DAY)
                .description("Ngày Quốc Tế Lao Động")
                .name("Ngày Quốc Tế Lao Động")
                .build());

        // Quốc Khánh (2/9)
        holidays.add(Holiday.builder()
                .startDate(LocalDate.of(LocalDate.now().getYear(), 9, 2))
                .endDate(LocalDate.of(LocalDate.now().getYear(), 9, 2)) // Có thể thêm 1 ngày nghỉ kèm tùy năm
//                .type(HolidayType.SINGLE_DAY)
                .description("Ngày Quốc Khánh")
                .name("Ngày Quốc Khánh")
                .build());

        // Lưu danh sách các ngày nghỉ lễ vào cơ sở dữ liệu
        holidayRepository.saveAll(holidays);
    }

    private void createAdminAccount() {
        String adminEmail = "a";
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
                    .password(passwordEncoder.encode("a"))
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

    private void seedTopics() {
        // Tạo 3 chủ đề academic
        for (int i = 1; i <= 3; i++) {
            Topic academicTopic = Topic.builder()
                    .name("Academic Topic " + i)
                    .type(TopicType.ACADEMIC)
                    .build();
            topicRepository.save(academicTopic);
        }

        // Tạo 3 chủ đề non-academic
        for (int i = 1; i <= 3; i++) {
            Topic nonAcademicTopic = Topic.builder()
                    .name("Non-Academic Topic " + i)
                    .type(TopicType.NON_ACADEMIC)
                    .build();
            topicRepository.save(nonAcademicTopic);
        }
    }

    private void createStudentAccounts() {
        List<Specialization> specializations = specializationRepository.findAll();
        List<String> maleNames = List.of("John", "Michael", "David", "James", "Robert", "William", "Charles", "Joseph", "Daniel", "Matthew");
        List<String> femaleNames = List.of("Emily", "Olivia", "Sophia", "Isabella", "Emma", "Ava", "Mia", "Amelia", "Charlotte", "Harper");

        List<Student> students = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            students.add(createSingleStudentAccount(i, maleNames.get(i), "sm" + (i + 1), Gender.MALE, "SE11" + String.format("%04d", i + 1), specializations.getFirst()));
        }

        for (int i = 0; i < 10; i++) {
            createSingleStudentAccount(i, femaleNames.get(i), "sf" + (i + 1), Gender.FEMALE, "SE11" + String.format("%04d", i + 11), specializations.getFirst());
        }

        seedDemandProblemTagsAndCounselingDemands(students.subList(0, 5));
    }

    private void seedDemandProblemTagsAndCounselingDemands(List<Student> students) {
        List<SupportStaff> supportStaffs = supportStaffRepository.findAll();
        SupportStaff supportStaff = supportStaffs.getFirst();

        List<CounselingDemand> demands = new ArrayList<>();

        Counselor counselor = counselorRepository.findById(4L)
                .orElseThrow(() -> new RuntimeException("Counselor with ID 4 not found."));

        for (int i = 0; i < 5; i++) { // Tạo cho 5 học sinh đầu tiên
            CounselingDemand demand = CounselingDemand.builder()
                    .status(CounselingDemand.Status.WAITING)
                    .totalPoint(0)
                    .student(students.get(i))
                    .supportStaff(supportStaff)
                    .counselor(counselor)
                    .demandProblemTags(new ArrayList<>())
                    .build();

            List<DemandProblemTag> demandProblemTags = new ArrayList<>();

            // Tạo 3 DemandProblemTag cho mỗi CounselingDemand
            for (int j = 1; j <= 3; j++) {
                DemandProblemTag demandProblemTag = DemandProblemTag.builder()
                        .student(students.get(i))
                        .source("Generated Source " + j)
                        .tagName("Tag " + j)
                        .number(j)
                        .totalPoint(j * 10)
                        .demand(demand)
                        .build();
                demandProblemTags.add(demandProblemTag);
                demand.setTotalPoint(demand.getTotalPoint() + demandProblemTag.getTotalPoint());
            }

            demand.getDemandProblemTags().addAll(demandProblemTags);
            demands.add(demand);
        }

        counselingDemandRepository.saveAll(demands);
        injectCounselorIntoDemand(counselor);
        logger.info("Seeded Counseling Demands and Demand Problem Tags.");
    }

    private void injectCounselorIntoDemand(Counselor counselor) {
//        List<CounselingDemand> demands = counselingDemandRepository.findAll();
//        for(CounselingDemand demand : demands) {
//            if (demand.getCounselor() == null) { // Chỉ gán counselor nếu demand chưa có counselor
//                demand.setCounselor(counselor);
//            }
//        }
//
//        counselingDemandRepository.saveAll(demands);
    }

    private Student createSingleStudentAccount(int index, String fullName, String studentEmail, Gender gender, String studentCode, Specialization specialization) {
        logger.info("Checking if student account with email '{}' exists.", studentEmail);

        if (accountRepository.findAccountByEmail(studentEmail).isEmpty()) {
            logger.info("Student account does not exist. Creating new student account.");

            Account student = Account.builder()
                    .email(studentEmail)
                    .role(Role.STUDENT)
                    .status(Status.ACTIVE)
                    .build();

            accountRepository.save(student);

            LoginType studentLoginType = LoginType.builder()
                    .password(passwordEncoder.encode("s"))
                    .method(LoginMethod.DEFAULT)
                    .account(student)
                    .build();

            loginTypeRepository.save(studentLoginType);

            // Create and save Profile for the student account
            Student studentProfile = Student.builder()
                    .account(student)
                    .fullName(fullName)
                    .phoneNumber("1234567890") // You can change this to generate different phone numbers if needed
                    .avatarLink(gender == Gender.MALE ? "https://png.pngtree.com/png-vector/20240204/ourlarge/pngtree-avatar-job-student-flat-portrait-of-man-png-image_11606889.png" : "https://thumbs.dreamstime.com/z/girl-avatar-face-student-schoolgirl-isolated-white-background-cartoon-style-vector-illustration-233213085.jpg")
                    .dateOfBirth(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()) // Sample DOB
                    .studentCode(studentCode)
                    .gender(gender)
                    .specialization(specialization)
                    .major(specialization.getMajor())
                    .department(specialization.getMajor().getDepartment())
                    .build();

            StudentCounselingProfile counselingProfile;
            if (index >= 0 && index <= 5) { // Chỉ sinh viên đầu tiên có tất cả thông tin rỗng
                counselingProfile = null;
            } else { // Từ sinh viên thứ hai trở đi thì đã có thông tin đầy đủ
                counselingProfile = StudentCounselingProfile.builder()
                        .student(studentProfile)
                        .introduction("introduction")// Set the student reference
                        .currentHealthStatus("Healthy") // Sample data
                        .psychologicalStatus("Stable")
                        .stressFactors("Low stress")
                        .academicDifficulties("None")
                        .studyPlan("Plan to excel in studies")
                        .careerGoals("Become a Software Engineer")
                        .partTimeExperience("Intern at Tech Company")
                        .internshipProgram("Summer Internship 2024")
                        .extracurricularActivities("Football Club")
                        .personalInterests("Reading, Traveling")
                        .socialRelationships("Good relationships with peers")
                        .financialSituation("Stable")
                        .financialSupport("Parents")
//                        .counselingIssue("General advice")
//                        .counselingGoal("Improve time management skills")
                        .desiredCounselingFields("Career Counseling, Mental Health")
                        .status(CounselingProfileStatus.VERIFIED)
                        .build();
            }

            studentProfile.setCounselingProfile(counselingProfile);

            Student returmStudent = profileRepository.save(studentProfile);

            if(index < 5) {
                for (int j = 0; j < 3; j++) {
                    Topic topic = topicRepository.findByType(TopicType.ACADEMIC).get(j);
                    QuestionCard questionCard = QuestionCard.builder()
                            .content("Nội dung câu hỏi cho " + fullName)
                            .questionType(QuestionType.ACADEMIC)
                            .student(studentProfile)
                            .status(QuestionCardStatus.PENDING)
                            .topic(topic) // Gán topic vào QuestionCard
                            .build();

                    questionCardRepository.save(questionCard);
                }
            }

            logger.info("Student account created with email '{}'.", studentEmail);

            return returmStudent;
        } else {
            logger.warn("Student account with email '{}' already exists.", studentEmail);
        }

        return null;
    }

    private void createManagerAccount() {
        String managerEmail = "m";
        logger.info("Checking if manager account with email '{}' exists.", managerEmail);

        if (accountRepository.findAccountByEmail(managerEmail).isEmpty()) {
            logger.info("Manager account does not exist. Creating new manager account.");

            Account manager = Account.builder()
                    .email(managerEmail)
                    .role(Role.MANAGER)
                    .status(Status.ACTIVE)
                    .build();

            accountRepository.save(manager);

            LoginType managerLoginType = LoginType.builder()
                    .password(passwordEncoder.encode("m"))
                    .method(LoginMethod.DEFAULT)
                    .account(manager)
                    .build();

            loginTypeRepository.save(managerLoginType);

            Profile managerProfile = Profile.builder()
                    .account(manager)
                    .fullName("Manager")
                    .phoneNumber("0987654321")
                    .avatarLink("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT_L2rcFrALpfz1YvxwvK2PYh__MYyv8XzpZw&s")
                    .dateOfBirth(LocalDate.of(1985, 5, 15)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli())
                    .gender(Gender.MALE)
                    .build();

            profileRepository.save(managerProfile);

            logger.info("Manager account created with email '{}'.", managerEmail);
        } else {
            logger.warn("Manager account with email '{}' already exists.", managerEmail);
        }
    }

    private void createSupportStaffAccount() {
        String supportStaffEmail = "ss";
        logger.info("Checking if support staff account with email '{}' exists.", supportStaffEmail);

        if (accountRepository.findAccountByEmail(supportStaffEmail).isEmpty()) {
            logger.info("Support staff account does not exist. Creating new support staff account.");

            Account supportStaff = Account.builder()
                    .email(supportStaffEmail)
                    .role(Role.SUPPORT_STAFF)  // Assuming you have a SUPPORT_STAFF role
                    .status(Status.ACTIVE)
                    .build();

            accountRepository.save(supportStaff);

            LoginType supportLoginType = LoginType.builder()
                    .password(passwordEncoder.encode("ss"))
                    .method(LoginMethod.DEFAULT)
                    .account(supportStaff)
                    .build();

            loginTypeRepository.save(supportLoginType);

            SupportStaff supportProfile = SupportStaff.builder()
                    .account(supportStaff)
                    .fullName("Support staff")
                    .phoneNumber("0987654321")
                    .avatarLink("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTbcgVPXa2ROdMbYfGCTKjcL6KE9p-So1BaxQ&s")
                    .dateOfBirth(LocalDate.of(1990, 1, 1)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli())
                    .gender(Gender.FEMALE)  // Assuming the support staff is female
                    .status(SupportStaff.SupportStaffStatus.AVAILABLE)
                    .build();

            profileRepository.save(supportProfile);

            logger.info("Support staff account created with email '{}'.", supportStaffEmail);
        } else {
            logger.warn("Support staff account with email '{}' already exists.", supportStaffEmail);
        }
    }

    @Transactional
    private void createCounselorAccounts() {

        List<CounselingSlot> counselingSlots = counselingSlotRepository.findAll();

        // List of counselor full names in Vietnamese with unique numbers
        List<String> maleNames = List.of("Nguyễn Văn A1", "Nguyễn Văn A2", "Nguyễn Văn A3", "Nguyễn Văn A4",
                "Trần Văn B1", "Trần Văn B2", "Trần Văn B3", "Trần Văn B4",
                "Lê Văn C1", "Lê Văn C2", "Lê Văn C3", "Lê Văn C4",
                "Phạm Văn D1", "Phạm Văn D2", "Phạm Văn D3", "Phạm Văn D4",
                "Đỗ Văn E1", "Đỗ Văn E2", "Đỗ Văn E3", "Đỗ Văn E4",
                "Hoàng Văn F1", "Hoàng Văn F2", "Hoàng Văn F3", "Hoàng Văn F4");

        List<String> femaleNames = List.of("Nguyễn Thị G1", "Nguyễn Thị G2", "Nguyễn Thị G3", "Nguyễn Thị G4",
                "Trần Thị H1", "Trần Thị H2", "Trần Thị H3", "Trần Thị H4",
                "Lê Thị I1", "Lê Thị I2", "Lê Thị I3", "Lê Thị I4",
                "Phạm Thị J1", "Phạm Thị J2", "Phạm Thị J3", "Phạm Thị J4",
                "Đỗ Thị K1", "Đỗ Thị K2", "Đỗ Thị K3", "Đỗ Thị K4",
                "Hoàng Thị L1", "Hoàng Thị L2", "Hoàng Thị L3", "Hoàng Thị L4");

        // List of specialization names
//        List<String> specializationNames = List.of("Khoa học tâm lý", "Giáo dục", "Kinh tế");

        // 1. Seed Department "Công nghệ thông tin"
        Department itDepartment = departmentRepository.findByName("Công nghệ thông tin")
                .orElseGet(() -> departmentRepository.save(Department.builder().name("Công nghệ thông tin").code("IT").build()));

        // 2. Seed Major "Software" for IT Department
        Major softwareMajor = majorRepository.findByName("Software")
                .orElseGet(() -> majorRepository.save(Major.builder().name("Software").code("IT01").department(itDepartment).build()));

        // 3. Seed Specializations "Backend" and "Frontend" for Software Major
        List<String> specializationNamesIT = List.of("Backend", "Frontend");
        List<Specialization> specializationListIT = specializationNamesIT.stream()
                .map(name -> specializationRepository.findByName(name)
                        .orElseGet(() -> specializationRepository.save(Specialization.builder().name(name).major(softwareMajor).build())))
                .toList();

        // 4. Seed Department "Business Analysis"
        Department baDepartment = departmentRepository.findByName("Business Analysis")
                .orElseGet(() -> departmentRepository.save(Department.builder().name("Business Analysis").code("BA").build()));

        // 5. Seed Major "Marketing" for BA Department
        Major marketingMajor = majorRepository.findByName("Marketing")
                .orElseGet(() -> majorRepository.save(Major.builder().name("Marketing").code("BA01").department(baDepartment).build()));

        // 6. Seed Specialization "Marketing" for Marketing Major
        List<String> specializationNamesBA = List.of("Marketing");
        List<Specialization> specializationListBA = specializationNamesBA.stream()
                .map(name -> specializationRepository.findByName(name)
                        .orElseGet(() -> specializationRepository.save(Specialization.builder().name(name).major(marketingMajor).build())))
                .toList();

        // List of expertise names
        List<String> expertiseNames = List.of("Tâm lý học", "Tư vấn gia đình", "Tư vấn nghề nghiệp");

//        // Creating specialization entities if they don't exist
//        List<Specialization> specializationList = specializationNames.stream()
//                .map(name -> specializationRepository.findByName(name)
//                        .orElseGet(() -> specializationRepository.save(Specialization.builder().name(name).build())))
//                .toList();

        // Creating expertise entities if they don't exist
        List<Expertise> expertiseList = expertiseNames.stream()
                .map(name -> expertiseRepository.findByName(name)
                        .orElseGet(() -> expertiseRepository.save(Expertise.builder().name(name).build())))
                .toList();

        List<Specialization> specializationList = specializationRepository.findAll();

        int maleIndex = 0;
        int femaleIndex = 0;

        // Create Academic Counselors
        for (Specialization specialization : specializationList) {
            for (int i = 0; i < 4; i++) {
                if (femaleIndex <= 11) {
                    createAcademicCounselor(femaleIndex, Gender.FEMALE, femaleNames.get(femaleIndex), specialization, counselingSlots);
                    femaleIndex++;
                }
            }
        }

        // Create Academic Counselors
        for (Specialization specialization : specializationList) {
            for (int i = 0; i < 4; i++) {
                if (maleIndex <= 11) {
                    createAcademicCounselor(maleIndex, Gender.MALE, maleNames.get(maleIndex), specialization, counselingSlots);
                    maleIndex++;
                }
            }
        }

        maleIndex = 0;
        femaleIndex = 0;

        // Create Non-Academic Counselors
        for (Expertise expertise : expertiseList) {
            for (int i = 0; i < 4; i++) { // 4 counselors per expertise
                if (femaleIndex <= 11) {
                    createNonAcademicCounselor(femaleIndex, Gender.FEMALE, femaleNames.get(femaleIndex), expertise, counselingSlots);
                    femaleIndex++;
                }
            }
        }

        for (Expertise expertise : expertiseList) {
            for (int i = 0; i < 4; i++) { // 4 counselors per expertise
                if (maleIndex <= 11) {
                    createNonAcademicCounselor(maleIndex, Gender.MALE, maleNames.get(maleIndex), expertise, counselingSlots);
                    maleIndex++;
                }
            }
        }
    }

    private void createAcademicCounselor(int index, Gender gender, String fullName, Specialization specialization, List<CounselingSlot> counselingSlots) {
        int[] startAndEnd = RandomUtil.getRandomStartEnd(0, counselingSlots.size(), 4);
        List<CounselingSlot> counselorSlots = getCounselorSlot(startAndEnd, counselingSlots);

        String counselorEmail = "ac" + ((gender == Gender.FEMALE) ? "f" : "m") + (index + 1);

        logger.info("Checking if academic counselor account with email '{}' exists.", counselorEmail);

        if (accountRepository.findAccountByEmail(counselorEmail).isEmpty()) {
            logger.info("Academic counselor account does not exist. Creating new account for specialization '{}'.", specialization.getName());

            Account counselor = Account.builder()
                    .email(counselorEmail)
                    .role(Role.ACADEMIC_COUNSELOR)
                    .status(Status.ACTIVE)
                    .build();

            accountRepository.save(counselor);

            LoginType counselorLoginType = LoginType.builder()
                    .password(passwordEncoder.encode("c"))
                    .method(LoginMethod.DEFAULT)
                    .account(counselor)
                    .build();

            loginTypeRepository.save(counselorLoginType);

            AcademicCounselor counselorProfile = AcademicCounselor.builder()
                    .account(counselor)
                    .fullName(fullName + " " + (index + 1))
                    .phoneNumber("123456789" + index)
                    .avatarLink(gender == Gender.MALE
                            ? "https://png.pngtree.com/png-vector/20230903/ourmid/pngtree-man-avatar-isolated-png-image_9935819.png"
                            : "https://static.vecteezy.com/system/resources/thumbnails/004/899/680/small/beautiful-blonde-woman-with-makeup-avatar-for-a-beauty-salon-illustration-in-the-cartoon-style-vector.jpg") // Set a default avatar
                    .dateOfBirth(LocalDate.of(1980 + index % 10, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .rating(BigDecimal.valueOf(4.0))
                    .gender(gender) // Adjust as needed
                    .specialization(specialization)
                    .major(specialization.getMajor())
                    .department(specialization.getMajor().getDepartment())
                    .status(CounselorStatus.AVAILABLE)
                    .counselingSlots(counselorSlots)
                    .academicDegree("Thạc sĩ") // Adjust degree as needed
                    .build();

            AvailableDateRange availableDateRange = createAvailableDateRangeFromTodayToTwoMonths(counselorProfile);
            counselorProfile.setAvailableDateRange(availableDateRange);

            profileRepository.save(counselorProfile);
            logger.info("Academic counselor account created with email '{}'.", counselorEmail);
        } else {
            logger.warn("Academic counselor account with email '{}' already exists.", counselorEmail);
        }
    }

    private void createNonAcademicCounselor(int index, Gender gender, String fullName, Expertise expertise, List<CounselingSlot> counselingSlots) {
        int[] startAndEnd = RandomUtil.getRandomStartEnd(0, counselingSlots.size(), 4);
        List<CounselingSlot> counselorSlots = getCounselorSlot(startAndEnd, counselingSlots);

        String counselorEmail = "nac" + ((gender == Gender.FEMALE) ? "f" : "m") + (index + 1);

        logger.info("Checking if non-academic counselor account with email '{}' exists.", counselorEmail);

        if (accountRepository.findAccountByEmail(counselorEmail).isEmpty()) {
            logger.info("Non-academic counselor account does not exist. Creating new account for expertise '{}'.", expertise.getName());

            Account counselor = Account.builder()
                    .email(counselorEmail)
                    .role(Role.NON_ACADEMIC_COUNSELOR)
                    .status(Status.ACTIVE)
                    .build();

            accountRepository.save(counselor);

            LoginType counselorLoginType = LoginType.builder()
                    .password(passwordEncoder.encode("c"))
                    .method(LoginMethod.DEFAULT)
                    .account(counselor)
                    .build();

            loginTypeRepository.save(counselorLoginType);

            NonAcademicCounselor counselorProfile = NonAcademicCounselor.builder()
                    .account(counselor)
                    .fullName(fullName + " " + (index + 1))
                    .phoneNumber("123456789" + index)
                    .avatarLink(gender == Gender.MALE
                            ? "https://png.pngtree.com/png-vector/20230903/ourmid/pngtree-man-avatar-isolated-png-image_9935819.png"
                            : "https://static.vecteezy.com/system/resources/thumbnails/004/899/680/small/beautiful-blonde-woman-with-makeup-avatar-for-a-beauty-salon-illustration-in-the-cartoon-style-vector.jpg") // Set a default avatar
                    .dateOfBirth(LocalDate.of(1980 + index % 10, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .rating(BigDecimal.valueOf(4.0))
                    .gender(gender) // Adjust as needed
                    .expertise(expertise)
                    .industryExperience(5) // Set an example for industry experience
                    .status(CounselorStatus.AVAILABLE)
                    .counselingSlots(counselorSlots)
                    .build();

            AvailableDateRange availableDateRange = createAvailableDateRangeFromTodayToTwoMonths(counselorProfile);
            counselorProfile.setAvailableDateRange(availableDateRange);

            profileRepository.save(counselorProfile);
            logger.info("Non-academic counselor account created with email '{}'.", counselorEmail);
        } else {
            logger.warn("Non-academic counselor account with email '{}' already exists.", counselorEmail);
        }
    }

    public AvailableDateRange createAvailableDateRangeFromTodayToTwoMonths(Counselor counselor) {
        LocalDate startDate = LocalDate.now();

        LocalDate endDate = startDate.plusMonths(2);

        return AvailableDateRange.builder()
                .startDate(startDate)
                .endDate(endDate)
                .counselor(counselor)
                .build();
    }

    private List<CounselingSlot> getCounselorSlot(int[] startAndEnd, List<CounselingSlot> counselingSlots) {
        List<CounselingSlot> counselorSlots = new ArrayList<>();
        for(int i = startAndEnd[0]; i < startAndEnd[1]; i++) {
            counselorSlots.add(counselingSlots.get(i));
        }
        return counselorSlots;
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
                    .name("Slot " + i)
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
                    .name("Slot " + i)
                    .endTime(endTime)
                    .build();

            counselingSlotRepository.save(slot);

            // Cập nhật thời gian bắt đầu và kết thúc cho slot tiếp theo
            startTime = endTime.plusMinutes(15); // Thêm khoảng cách giữa các slot
            endTime = startTime.plusHours(1).plusMinutes(0); // Thêm thời gian cho slot tiếp theo
        }
    }

    //    private void createCounselingAppointmentRequest(Counselor counselor) {
//
//        Student student = studentRepository.findById(2L).orElseThrow(() -> new NotFoundException("Student Not Found"));
//
//        CounselingAppointmentRequest appointmentRequest = CounselingAppointmentRequest.builder()
//                .requireDate(LocalDate.of(2024, 9, 13)) // Ngày 16/11/2024
//                .startTime(LocalTime.of(8, 0)) // 08:30
//                .endTime(LocalTime.of(9, 0)) // Ví dụ giờ kết thúc
//                .status(CounselingAppointmentRequestStatus.APPROVED)
//                .meetingType(MeetingType.ONLINE)
//                .reason("Counseling session")
//                .counselor(counselor)
//                .student(student)
//                .build();
//
//        counselingAppointmentRequestRepository.save(appointmentRequest);
//
//        createOnlineCounselingAppointment(appointmentRequest);
//    }
//
//    private void createCounselingAppointmentRequest2(Counselor counselor) {
//
//        Student student = studentRepository.findById(2L).orElseThrow(() -> new NotFoundException("Student Not Found"));
//
//        CounselingAppointmentRequest appointmentRequest = CounselingAppointmentRequest.builder()
//                .requireDate(LocalDate.of(2024, 9, 20)) // Ngày 16/11/2024
//                .startTime(LocalTime.of(9, 15)) // 08:30
//                .endTime(LocalTime.of(10, 15)) // Ví dụ giờ kết thúc
//                .status(CounselingAppointmentRequestStatus.APPROVED)
//                .meetingType(MeetingType.OFFLINE)
//                .reason("Counseling session")
//                .counselor(counselor)
//                .student(student)
//                .build();
//
//        counselingAppointmentRequestRepository.save(appointmentRequest);
//
//        createOfflineCounselingAppointment(appointmentRequest);
//    }

//    private void createCategories() {
//        // Tạo 4 danh mục
//        for (int i = 1; i <= 4; i++) {
//            Category category = new Category("CODE" + i, "Category " + i);
//            categoryRepository.save(category);
//        }
//    }
//
//    private void createSemesters() {
//        // Tạo các học kỳ cho năm 2024 và 2025
//        List<Semester> semesters = new ArrayList<>();
//        String[] semesterNames = {"Summer", "Fall", "Spring"};
//        LocalDate startDate;
//        LocalDate endDate;
//
//        for (int year = 2024; year <= 2025; year++) {
//            for (int i = 0; i < semesterNames.length; i++) {
//                switch (semesterNames[i]) {
//                    case "Summer":
//                        startDate = LocalDate.of(year, 1, 1);
//                        endDate = LocalDate.of(year, 4, 30);
//                        break;
//                    case "Fall":
//                        startDate = LocalDate.of(year, 5, 1);
//                        endDate = LocalDate.of(year, 8, 31);
//                        break;
//                    case "Spring":
//                        startDate = LocalDate.of(year, 9, 1);
//                        endDate = LocalDate.of(year, 12, 31);
//                        break;
//                    default:
//                        continue; // Nếu không phải là học kỳ hợp lệ thì bỏ qua
//                }
//                Semester semester = Semester.builder()
//                        .semesterCode(semesterNames[i] + year)
//                        .name(semesterNames[i] + " " + year)
//                        .startDate(startDate)
//                        .endDate(endDate)
//                        .build();
//                semesters.add(semester);
//            }
//        }
//
//        semesterRepository.saveAll(semesters);
//
//        // Tạo sự kiện và TrainingPoint cho từng học kỳ
//        for (Semester semester : semesters) {
//            createEventsForSemester(semester);
//            createTrainingPointsForSemester(semester);
//        }
//    }
//
//    private void createTrainingPointsForSemester(Semester semester) {
//        // Tạo TrainingPoint cho mỗi sinh viên
//        List<Student> students = studentRepository.findAll();
//        for (Student student : students) {
//            TrainingPoint trainingPoint = TrainingPoint.builder()
//                    .student(student)
//                    .point(65) // Hoặc bất kỳ điểm nào bạn muốn khởi tạo
//                    .semester(semester)
//                    .build();
//
//            // Lưu TrainingPoint vào database
//            trainingPointRepository.save(trainingPoint);
//        }
//    }
//
//    private void createEventsForSemester(Semester semester) {
//        // Lấy danh sách các danh mục
//        List<Category> categories = categoryRepository.findAll();
//
//        for (int i = 1; i <= 2; i++) { // Tạo 5 sự kiện cho mỗi học kỳ
//            String eventContent = "This is the detailed content for Event " + i + " in " + semester.getName();
//            Event event = Event.builder()
//                    .title("Event " + i + " - " + semester.getName())
//                    .content(eventContent)
//                    .displayImage("https://www.mecc.nl/wp-content/uploads/2021/12/Header_zakelijk_event_IC_1440x600.jpg")
//                    .view(0)
//                    .isNeedAccept(false)
//                    .category(categories.get(i % categories.size()))
//                    .semester(semester)
//                    .build();
//
//            eventRepository.save(event);
//
//            // Tạo lịch cho các sự kiện trong phạm vi thời gian của học kỳ
//            LocalDateTime startDateTime = semester.getStartDate().atTime(10, 0) // Bắt đầu vào 10:00 của ngày bắt đầu học kỳ
//                    .plusDays(i * 9); // Thay đổi ngày bắt đầu cho từng sự kiện
//            LocalDateTime endDateTime = startDateTime.plusHours(2); // Kéo dài 2 tiếng
//
//            // Đảm bảo thời gian kết thúc không vượt quá ngày kết thúc của học kỳ
//            if (endDateTime.isAfter(semester.getEndDate().atTime(23, 59))) {
//                endDateTime = semester.getEndDate().atTime(23, 59);
//            }
//
//            EventSchedule schedule = EventSchedule.builder()
//                    .event(event)
//                    .startDate(startDateTime)
//                    .endDate(endDateTime)
//                    .maxParticipants(10)
//                    .currentParticipants(0)
//                    .address("Address for Event " + i)
//                    .build();
//
//            eventScheduleRepository.save(schedule);
//
//            // Tạo RecapVideo
//            RecapVideo recapVideo = RecapVideo.builder()
//                    .event(event)
//                    .videoUrl("https://res.cloudinary.com/dd8y8sska/video/upload/v1727066591/video/hulrayq22xw75ofaoxrg.mp4")
//                    .build();
//            recapVideoRepository.save(recapVideo);
//
//            // Tạo ContentImage
//            ContentImage contentImage = ContentImage.builder()
//                    .event(event)
//                    .imageUrl("https://res.cloudinary.com/dd8y8sska/image/upload/v1724948516/cld-sample-5.jpg")
//                    .build();
//            contentImageRepository.save(contentImage);
//            ContentImage contentImage2 = ContentImage.builder()
//                    .event(event)
//                    .imageUrl("https://res.cloudinary.com/dd8y8sska/image/upload/v1724948516/cld-sample-5.jpg")
//                    .build();
//            contentImageRepository.save(contentImage2);
//        }
//    }

}

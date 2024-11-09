package com.capstone2024.scss.infrastructure.data;

import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.entities.LoginType;
import com.capstone2024.scss.domain.account.entities.Profile;
import com.capstone2024.scss.domain.account.enums.LoginMethod;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.account.enums.Status;
import com.capstone2024.scss.domain.common.entity.Semester;
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
import com.capstone2024.scss.infrastructure.data.fap.dto.*;
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
import com.capstone2024.scss.infrastructure.repositories.demand.*;
import com.capstone2024.scss.infrastructure.repositories.student.CounselingProfileRepository;
import com.capstone2024.scss.infrastructure.repositories.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
    private final RestTemplate restTemplate;
    @Value("${server.api.fap.system.base.url}")
    private String fapServerUrl;

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
    private final SemesterRepository semesterRepository;
    private final DemandProblemTagRepository demandProblemTagRepository;

    @Override
    public void run(String... args) throws Exception {
        seedTopics();
        seedProblemTags();
        seedVietnamHolidays();
        seedSlots();
        seedSemesters();
        seedDepartments();

        seedAdminAccount();
        seedManagerAccount();
        seedSupportStaffAccount();
        seedCounselorAccounts();
        seedStudentAccounts();
        seedStudentProblemTags();
        
        generatePromptToOpenAI("Học sinh này học quá tệ, không thường xuyên hỗ trợ làm bài tập nhóm, thường xuyên ngủ gật");
    }

    private void generatePromptToOpenAI(String prompt) {
        List<String> predefinedTags = problemTagRepository.findAll()
                .stream()
                .map(ProblemTag::getName)
                .collect(Collectors.toList());

        String preDefinedTags = String.join(", ", predefinedTags);

        String promptCommand = "Pre-defined tags: [" + preDefinedTags + "]\n" +
                "Your action: [\n" +
                "Content Analysis: Before tagging, analyze the prompt to identify its meaning, sentiment, and specific behaviors related to the student.\n" +
                "Filter Out Stop Words: Remove unnecessary stop words to simplify the sentence and focus on key terms related to behavior and academic performance.\n" +
                "Match with Available Tags: Based on the filtered keywords, compare them with the available tags to find the most relevant ones.\n" +
                "Assign Multiple Tags if Necessary: In cases where multiple aspects of behavior are described, multiple tags may be assigned, with each tag representing a specific negative behavior or area of weakness.\n" +
                "]\n" +
                "Response: [JSON format with key (result) and value (String array of tags)]";

        System.out.println(promptCommand);
    }

    @Transactional
    private void seedStudentProblemTags() {
        List<Student> students = studentRepository.findAll();
        for(Student student : students) {
            ResponseEntity<DemandProblemTagFapResponseDTO[]> response = restTemplate.getForEntity(fapServerUrl + "/api/problem-tags/" + student.getStudentCode(), DemandProblemTagFapResponseDTO[].class);
            DemandProblemTagFapResponseDTO[] responseBody = response.getBody();
            Map<String, Integer> tagMap = new HashMap<>();
            if(responseBody != null) {
                List<DemandProblemTagFapResponseDTO> body = new ArrayList<>(List.of(responseBody));
                if(body.isEmpty()) continue;

                for(DemandProblemTagFapResponseDTO dto : body) {
                    if(tagMap.get(dto.getSemesterName() + dto.getSource()) == null) {
                        tagMap.put(dto.getSemesterName() + dto.getSource(), 1);
                    } else {
                        tagMap.put(dto.getSemesterName() + dto.getSource(), tagMap.get(dto.getSemesterName() + dto.getSource()) + 1);
                    }
                    Optional<ProblemTag> problemTag = problemTagRepository.findByName(dto.getProblemTagName());
                    Optional<Semester> semester = semesterRepository.findByName(dto.getSemesterName());
                    if(problemTag.isEmpty() || semester.isEmpty()) {
                        throw new NotFoundException(dto.getProblemTagName() + ", " + dto.getSemesterName());
                    }
                    demandProblemTagRepository.save(DemandProblemTag.builder()
                                    .problemTag(problemTag.get())
                                    .source(dto.getSource())
                                    .semester(semester.get())
                                    .student(student)
                                    .isExcluded(semester.get().getName().equals("Summer2024") || !(tagMap.get(dto.getSemesterName() + dto.getSource()) != null && tagMap.get(dto.getSemesterName() + dto.getSource()) > 1))
                            .build());
                }
            }
        }
    }

    public void seedSemesters() {
        // Gọi API để lấy danh sách SemesterFapResponseDTO
        ResponseEntity<SemesterFapResponseDTO[]> response = restTemplate.getForEntity(fapServerUrl + "/api/academic/semesters", SemesterFapResponseDTO[].class);
        SemesterFapResponseDTO[] semesterDTOs = response.getBody();

        if (semesterDTOs != null) {
            for (SemesterFapResponseDTO semesterDTO : semesterDTOs) {
                // Tạo và lưu Semester
                Semester semester = Semester.builder()
                        .name(semesterDTO.getName())
                        .build();
                semesterRepository.save(semester);
            }
        }
    }

    public void seedDepartments() {
        // Gọi API để lấy danh sách DepartmentFapResponseDTO
        ResponseEntity<DepartmentFapResponseDTO[]> response = restTemplate.getForEntity(fapServerUrl + "/api/academic/departments", DepartmentFapResponseDTO[].class);
        DepartmentFapResponseDTO[] departmentDTOs = response.getBody();

        if (departmentDTOs != null) {
            for (DepartmentFapResponseDTO departmentDTO : departmentDTOs) {
                // Tạo và lưu Department
                Department department = Department.builder()
                        .name(departmentDTO.getName())
                        .code(departmentDTO.getCode())
                        .build();
                departmentRepository.save(department);

                // Tạo và lưu Major và Specialization cho mỗi Department
                for (MajorFapResponseDTO majorDTO : departmentDTO.getMajors()) {
                    Major major = Major.builder()
                            .name(majorDTO.getName())
                            .code(majorDTO.getCode())
                            .department(department)
                            .build();
                    majorRepository.save(major);

                    for (SpecializationFapResponseDTO specializationDTO : majorDTO.getSpecializations()) {
                        Specialization specialization = Specialization.builder()
                                .name(specializationDTO.getName())
                                .code(specializationDTO.getCode())
                                .major(major)
                                .build();
                        specializationRepository.save(specialization);
                    }
                }
            }
        }
    }

//    @Transactional
//    private void seedSemesters() {
//        List<String> semesterNames = List.of(
//                "Spring2018", "Summer2018", "Fall2018",
//                "Spring2019", "Summer2019", "Fall2019",
//                "Spring2020", "Summer2020", "Fall2020",
//                "Spring2021", "Summer2021", "Fall2021",
//                "Spring2022", "Summer2022", "Fall2022",
//                "Spring2023", "Summer2023", "Fall2023",
//                "Spring2024", "Summer2024", "Fall2024"
//        );
//
//        for (String semesterName : semesterNames) {
//            Semester semester = new Semester();
//            semester.setName(semesterName);
//            semesterRepository.save(semester);
//        }
//    }

    @Transactional
    public void seedProblemTags() {
        if (problemTagRepository.count() > 0) {
            return; // If data already exists, do not seed again
        }

        // Create or retrieve the "Study" category and other categories
        ProblemCategory studyCategory = problemCategoryRepository.findByName("Study")
                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Study").build()));

        problemCategoryRepository.findByName("Extracurricular Activities")
                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Extracurricular Activities").build()));

        problemCategoryRepository.findByName("Event")
                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Event").build()));

        problemCategoryRepository.findByName("Club")
                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Club").build()));

        // Only create tags for the "Study" category
        List<ProblemTag> studyTags = List.of(
//                // Attitude and motivation for studying
//                ProblemTag.builder().name("Active Participation").category(studyCategory).build(),
//                ProblemTag.builder().name("Positive Study Attitude").category(studyCategory).build(),
//                ProblemTag.builder().name("Willingness to Learn").category(studyCategory).build(),
//
//                // Teamwork skills
//                ProblemTag.builder().name("Good Team Contribution").category(studyCategory).build(),
//                ProblemTag.builder().name("Supports Peers in Team").category(studyCategory).build(),
//                ProblemTag.builder().name("Good Team Communication").category(studyCategory).build(),
//
//                // Personal skills
//                ProblemTag.builder().name("Creative Thinking").category(studyCategory).build(),
//                ProblemTag.builder().name("Effective Problem Solving").category(studyCategory).build(),
//                ProblemTag.builder().name("Good Time Management Skills").category(studyCategory).build(),
//                ProblemTag.builder().name("Proactive in Learning").category(studyCategory).build(),
//
//                // Achievements and contributions
//                ProblemTag.builder().name("Completes Tasks on Time").category(studyCategory).build(),
//                ProblemTag.builder().name("Significant Improvement in Studies").category(studyCategory).build(),
//                ProblemTag.builder().name("Outstanding Class Contribution").category(studyCategory).build(),

                // Negative tags
                ProblemTag.builder().name("Lacks Study Motivation").category(studyCategory).build(),
                ProblemTag.builder().name("Uncooperative Attitude").category(studyCategory).build(),
                ProblemTag.builder().name("Low Participation in Class Activities").category(studyCategory).build(),
                ProblemTag.builder().name("Difficult to Work with in Teams").category(studyCategory).build(),
                ProblemTag.builder().name("Does Not Support Team Members").category(studyCategory).build(),
                ProblemTag.builder().name("Poor Team Communication").category(studyCategory).build(),
                ProblemTag.builder().name("Limited Thinking").category(studyCategory).build(),
                ProblemTag.builder().name("Slow in Problem Solving").category(studyCategory).build(),
                ProblemTag.builder().name("Lacks Time Management Skills").category(studyCategory).build(),
                ProblemTag.builder().name("Not Proactive in Learning").category(studyCategory).build(),
                ProblemTag.builder().name("Submits Late").category(studyCategory).build(),
                ProblemTag.builder().name("Does Not Complete Tasks").category(studyCategory).build(),
                ProblemTag.builder().name("No Improvement").category(studyCategory).build()
        );

        // Save tags for "Study" to the database
        problemTagRepository.saveAll(studyTags);
    }

    private void seedVietnamHolidays() {
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

    private void seedAdminAccount() {
        String adminEmail = "a";
        logger.info("Checking if admin account with email '{}' exists.", adminEmail);

        if (accountRepository.findAccountByEmail(adminEmail).isEmpty()) {
            logger.info("Admin account does not exist. Creating new admin account.");

            Account admin = Account.builder()
                    .email(adminEmail)
                    .role(Role.ADMINISTRATOR)
                    .status(Status.ACTIVE)
                    .password(passwordEncoder.encode("a"))
                    .build();

            accountRepository.save(admin);

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

    private void seedStudentAccounts() {
        ResponseEntity<StudentFapResponseDTO[]> response = restTemplate.getForEntity(fapServerUrl + "/api/students", StudentFapResponseDTO[].class);

        if (response.getBody() != null) {
            List<StudentFapResponseDTO> studentDTOs = List.of(response.getBody());

            List<Student> students = new ArrayList<>();
            for (int i = 0; i < studentDTOs.size(); i++) {
                StudentFapResponseDTO dto = studentDTOs.get(i);

                Department department = departmentRepository.findByName(dto.getDepartmentName()).orElse(null);
                Major major = majorRepository.findByName(dto.getMajorName()).orElse(null);
                Specialization specialization = specializationRepository.findByName(dto.getSpecializationName()).orElse(null);

                // Create the student account and skip counseling profile for the first two students
                Student student = createSingleStudentAccount(
                        dto.getStudentCode(),
                        dto.getFullName(),
                        dto.getEmail(),
                        dto.getGender(),
                        specialization,
                        major,
                        department,
                        i >= 2 // Only add StudentCounselingProfile if index is 2 or greater
                );
                students.add(student);
            }
//            profileRepository.saveAll(students);
        }

//        Specialization specialization = specializationRepository.findByName("NodeJS").orElse(null);
//        Department department = departmentRepository.findByName("Information Technology").orElse(null);
//        Major major = majorRepository.findByName("Software Engineering").orElse(null);
//
//        createSingleStudentAccount("SE170042", "Trình Vĩnh Phát", "phattvse170042@fpt.edu.vn", Gender.MALE, specialization, major, department, true);
//        createSingleStudentAccount("SE170440", "Đoàn Tiến Phát", "phatdtse170440@fpt.edu.vn", Gender.MALE, specialization, major, department, true);
//        createSingleStudentAccount("SE170225", "Vũ Ngọc Hải Đăng", "dangvnhse170225@fpt.edu.vn", Gender.MALE, specialization, major, department, true);
//        createSingleStudentAccount("SE170431", "Nguyễn An Khánh", "khanhnase170431@fpt.edu.vn", Gender.MALE, specialization, major, department, true);
    }

    private Student createSingleStudentAccount(
            String studentCode,
            String fullName,
            String studentEmail,
            Gender gender,
            Specialization specialization,
            Major major,
            Department department,
            boolean createCounselingProfile) { // New parameter to control counseling profile creation

        logger.info("Checking if student account with email '{}' exists.", studentEmail);

        if (accountRepository.findAccountByEmail(studentEmail).isEmpty()) {
            logger.info("Student account does not exist. Creating new student account.");

            Account studentAccount = Account.builder()
                    .email(studentEmail)
                    .role(Role.STUDENT)
                    .status(Status.ACTIVE)
                    .password(passwordEncoder.encode("s"))
                    .build();

            accountRepository.save(studentAccount);

            // Create Profile for the student account
            Student studentProfile = Student.builder()
                    .account(studentAccount)
                    .fullName(fullName)
                    .phoneNumber("1234567890")
                    .avatarLink(gender == Gender.MALE ? "https://png.pngtree.com/png-vector/20240204/ourlarge/pngtree-avatar-job-student-flat-portrait-of-man-png-image_11606889.png" : "https://thumbs.dreamstime.com/z/girl-avatar-face-student-schoolgirl-isolated-white-background-cartoon-style-vector-illustration-233213085.jpg")
                    .dateOfBirth(LocalDate.of(2000, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .studentCode(studentCode)
                    .gender(gender)
                    .specialization(specialization)
                    .major(major)
                    .department(department)
                    .build();

            // Conditionally add StudentCounselingProfile
            if (createCounselingProfile) {
                StudentCounselingProfile counselingProfile = StudentCounselingProfile.builder()
                        .student(studentProfile)
                        .introduction("Introduction")
                        .currentHealthStatus("Healthy")
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
                        .desiredCounselingFields("Career Counseling, Mental Health")
                        .status(CounselingProfileStatus.VERIFIED)
                        .build();

                studentProfile.setCounselingProfile(counselingProfile);
            }

            logger.info("Student account created with email '{}'.", studentEmail);
            return profileRepository.save(studentProfile);
        } else {
            logger.warn("Student account with email '{}' already exists.", studentEmail);
        }
        return null;
    }

    private void seedManagerAccount() {
        String managerEmail = "m";
        logger.info("Checking if manager account with email '{}' exists.", managerEmail);

        if (accountRepository.findAccountByEmail(managerEmail).isEmpty()) {
            logger.info("Manager account does not exist. Creating new manager account.");

            Account manager = Account.builder()
                    .email(managerEmail)
                    .role(Role.MANAGER)
                    .password(passwordEncoder.encode("m"))
                    .status(Status.ACTIVE)
                    .build();

            accountRepository.save(manager);

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

    private void seedSupportStaffAccount() {
        String supportStaffEmail = "ss";
        logger.info("Checking if support staff account with email '{}' exists.", supportStaffEmail);

        if (accountRepository.findAccountByEmail(supportStaffEmail).isEmpty()) {
            logger.info("Support staff account does not exist. Creating new support staff account.");

            Account supportStaff = Account.builder()
                    .email(supportStaffEmail)
                    .role(Role.SUPPORT_STAFF)  // Assuming you have a SUPPORT_STAFF role
                    .status(Status.ACTIVE)
                    .password(passwordEncoder.encode("ss"))
                    .build();

            accountRepository.save(supportStaff);

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
    private void seedCounselorAccounts() {

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

        // List of expertise names
        List<String> expertiseNames = List.of("Tâm lý học", "Tư vấn gia đình", "Tư vấn nghề nghiệp");

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
                    .password(passwordEncoder.encode("c"))
                    .build();

            accountRepository.save(counselor);

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
                    .password(passwordEncoder.encode("c"))
                    .build();

            accountRepository.save(counselor);

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

    public void seedSlots() {
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

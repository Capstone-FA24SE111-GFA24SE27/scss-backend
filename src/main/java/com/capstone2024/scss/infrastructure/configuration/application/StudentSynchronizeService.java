package com.capstone2024.scss.infrastructure.configuration.application;

import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.demand.dto.FollowStatusDTO;
import com.capstone2024.scss.application.notification.dtos.NotificationDTO;
import com.capstone2024.scss.application.student.dto.AttendanceDTO;
import com.capstone2024.scss.application.student.dto.AttendanceDetailDTO;
import com.capstone2024.scss.application.student.dto.StudyDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.account.enums.Status;
import com.capstone2024.scss.domain.common.entity.Semester;
import com.capstone2024.scss.domain.common.helpers.NotificationHelper;
import com.capstone2024.scss.domain.common.mapper.student.academic.StudentStudyMapper;
import com.capstone2024.scss.domain.common.utils.StringUtil;
import com.capstone2024.scss.domain.counselor.entities.Specialization;
import com.capstone2024.scss.domain.demand.entities.DemandProblemTag;
import com.capstone2024.scss.domain.demand.entities.ProblemTag;
import com.capstone2024.scss.domain.demand.entities.StudentFollowing;
import com.capstone2024.scss.domain.notification.services.NotificationService;
import com.capstone2024.scss.domain.student.entities.Department;
import com.capstone2024.scss.domain.student.entities.Major;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.entities.academic.AcademicTranscript;
import com.capstone2024.scss.domain.student.entities.academic.AttendanceDetail;
import com.capstone2024.scss.domain.student.entities.academic.StudentStudy;
import com.capstone2024.scss.domain.student.entities.academic.enums.AttendanceStatus;
import com.capstone2024.scss.domain.student.entities.academic.enums.StudyStatus;
import com.capstone2024.scss.infrastructure.configuration.openai.OpenAIService;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.RabbitMQConfig;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto.PasswordResetEmail;
import com.capstone2024.scss.infrastructure.data.DatabaseSeeder;
import com.capstone2024.scss.infrastructure.data.fap.dto.DepartmentFapResponseDTO;
import com.capstone2024.scss.infrastructure.data.fap.dto.MajorFapResponseDTO;
import com.capstone2024.scss.infrastructure.data.fap.dto.SpecializationFapResponseDTO;
import com.capstone2024.scss.infrastructure.data.fap.dto.StudentFapResponseDTO;
import com.capstone2024.scss.infrastructure.repositories.DepartmentRepository;
import com.capstone2024.scss.infrastructure.repositories.MajorRepository;
import com.capstone2024.scss.infrastructure.repositories.SemesterRepository;
import com.capstone2024.scss.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.scss.infrastructure.repositories.account.ProfileRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.SpecializationRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.DemandProblemTagRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.ProblemTagRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.StudentFollowingRepository;
import com.capstone2024.scss.infrastructure.repositories.student.StudentRepository;
import com.capstone2024.scss.infrastructure.repositories.student.academic.AcademicTranscriptRepository;
import com.capstone2024.scss.infrastructure.repositories.student.academic.AttendanceDetailRepository;
import com.capstone2024.scss.infrastructure.repositories.student.academic.StudentStudyRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentSynchronizeService {

    @Value("${server.api.fap.system.base.url}")
    private String fapServerUrl;
    @Value("${hibernate.jdbc.batch_size}")
    private Integer BATCH_SIZE;

    private final StudentRepository studentRepository;
    private final RestTemplate restTemplate;
    private final SemesterRepository semesterRepository;
    private final StudentStudyRepository studentStudyRepository;
    private final AttendanceDetailRepository attendanceDetailRepository;
    private final DepartmentRepository departmentRepository;
    private final MajorRepository majorRepository;
    private final SpecializationRepository specializationRepository;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AcademicTranscriptRepository academicTranscriptRepository;
    private final OpenAIService openAIService;
    private final DemandProblemTagRepository demandProblemTagRepository;
    private final ProblemTagRepository problemTagRepository;
    private final StudentFollowingRepository studentFollowingRepository;
    private final NotificationService notificationService;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void syncAllStudent() {
        List<Student> students = studentRepository.findAll();
        for (Student student : students) {
            syncOneStudent(student.getStudentCode());
        }
    }

    @Transactional
    public void syncListStudent(List<String> studentCodes) {
        for (String studentCode : studentCodes) {
            syncOneStudent(studentCode);
        }
    }

    private Student createSingleStudentAccount(
            StudentFapResponseDTO dto,
            Specialization specialization,
            Major major,
            Department department) { // New parameter to control counseling profile creation

        logger.info("Checking if student account with email '{}' exists.", dto.getEmail());

        if (accountRepository.findAccountByEmail(dto.getEmail()).isEmpty()) {
            logger.info("Student account does not exist. Creating new student account.");

            Account studentAccount = Account.builder()
                    .email(dto.getEmail())
                    .role(Role.STUDENT)
                    .status(Status.ACTIVE)
                    .password(passwordEncoder.encode(StringUtil.getUsernameFromEmail(dto.getEmail())))
                    .build();

            accountRepository.save(studentAccount);

            // Create Profile for the student account
            Student studentProfile = Student.builder()
                    .account(studentAccount)
                    .fullName(dto.getFullName())
                    .phoneNumber(dto.getPhoneNumber())
                    .avatarLink(dto.getAvatarLink())
                    .dateOfBirth(dto.getDateOfBirth())
                    .address(dto.getAddress())
                    .studentCode(dto.getStudentCode())
                    .gender(dto.getGender())
                    .specialization(specialization)
                    .major(major)
                    .department(department)
                    .build();

            logger.info("Student account created with email '{}'.", dto.getEmail());

            PasswordResetEmail emailData = new PasswordResetEmail(dto.getEmail(), "Initial password for SCSS system", StringUtil.getUsernameFromEmail(dto.getEmail()));
            rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, emailData);

            return profileRepository.save(studentProfile);
        } else {
            logger.warn("Student account with email '{}' already exists.", dto.getEmail());
        }
        return null;
    }

    @Transactional
    public void syncOneStudent(String studentCode) {
        Optional<Student> studentOptional = studentRepository.findByStudentCode(studentCode);
        Student student = null;
        if (studentOptional.isEmpty()) {
            ResponseEntity<StudentFapResponseDTO> response = restTemplate.getForEntity(fapServerUrl + "/api/students/" + studentCode, StudentFapResponseDTO.class);
            if (!response.getStatusCode().is4xxClientError()) {
                StudentFapResponseDTO dto = response.getBody();
                Department department = departmentRepository.findByName(dto.getDepartmentName()).orElse(null);
                Major major = majorRepository.findByName(dto.getMajorName()).orElse(null);
                Specialization specialization = specializationRepository.findByName(dto.getSpecializationName()).orElse(null);

                // Create the student account and skip counseling profile for the first two students
                student = createSingleStudentAccount(
                        dto,
                        specialization,
                        major,
                        department
                );
            } else {
                throw new NotFoundException("Not Found Student");
            }
        } else {
            student = studentOptional.get();
        }
        saveStudyAndAttendanceForOneStudent(student);
        saveAcademicTranscriptForStudent(student);
    }

    private void saveAcademicTranscriptForStudent(Student student) {
        List<AcademicTranscript> academicTranscripts = new ArrayList<>();
        Map<String, AcademicTranscript> academicTranscriptMap = getAcademicTrancriptMap(student);
        boolean isStudyChange = false;

        String url = fapServerUrl + "/api/studies/" + student.getStudentCode();
        StudyDTO[] studiesArray = restTemplate.getForObject(url, StudyDTO[].class);
        if (studiesArray != null) {
            for (StudyDTO fapDTO : studiesArray) {
                String key = fapDTO.getSubjectCode() + fapDTO.getTerm();
                AcademicTranscript academicTranscriptMapValue = academicTranscriptMap.get(key);
                if (academicTranscriptMapValue == null) {
                    Semester semester = semesterRepository.findByName(fapDTO.getSemester()).orElseGet(() -> {
//                        if(fapDTO.getSemester() != null) {
//                            // Tạo mới Semester nếu không tìm thấy
//                            Semester newSemester = Semester.builder()
//                                    .name(fapDTO.getSemester())
//                                    .build();
//                            return semesterRepository.save(newSemester); // Lưu và trả về bản ghi mới
//                        }
                        return null;
                    });
                    AcademicTranscript academicTranscript = StudentStudyMapper.toAcademicTranscript(fapDTO, student, semester);
                    academicTranscripts.add(academicTranscript);
                    isStudyChange = true;
                } else {
                    if(!academicTranscriptMapValue.getStatus().equals(StudyStatus.valueOf(fapDTO.getStatus()))) {
                        academicTranscriptMapValue.setStatus(StudyStatus.valueOf(fapDTO.getStatus()));
                        isStudyChange = true;
                    }
                    if(!academicTranscriptMapValue.getGrade().equals(fapDTO.getGrade())) {
                        academicTranscriptMapValue.setGrade(fapDTO.getGrade());
                        isStudyChange = true;
                    }
                    if(!academicTranscriptMapValue.getSemester().getName().equals(fapDTO.getSemester())) {
                        Optional<Semester> newSemester = semesterRepository.findByName(fapDTO.getSemester());
                        newSemester.ifPresent(academicTranscriptMapValue::setSemester);
                        isStudyChange = true;
                    }
                    if(isStudyChange) {
                        academicTranscripts.add(academicTranscriptMapValue);
                    }
                }
            }

            if (isStudyChange) {
                batchSaveAcademicTranscript(academicTranscripts);
            }
        }
    }

    private Map<String, AttendanceDetail> getAttendanceDetailMap(Student student) {
        List<StudentStudy> studentStudiesDB = studentStudyRepository.findByStudent(student);
        Map<String, AttendanceDetail> studentStudyMap = new HashMap<>();
        for (StudentStudy studentStudy : studentStudiesDB) {
            for (AttendanceDetail attendanceDetail : studentStudy.getAttendanceDetails()) {
                String key = studentStudy.getSubjectCode() + studentStudy.getSemester().getName() + attendanceDetail.getDate().toString() + attendanceDetail.getSlot();
                studentStudyMap.put(key, attendanceDetail);
            }
        }
        return studentStudyMap;
    }

    private Map<String, StudentStudy> getStudyMap(Student student) {
        List<StudentStudy> studentStudiesDB = studentStudyRepository.findByStudent(student);
        Map<String, StudentStudy> studentStudyMap = new HashMap<>();
        for (StudentStudy studentStudy : studentStudiesDB) {
            String key = studentStudy.getSubjectCode() + studentStudy.getSemester().getName();
            studentStudyMap.put(key, studentStudy);
        }
        return studentStudyMap;
    }

    private Map<String, AcademicTranscript> getAcademicTrancriptMap(Student student) {
        List<AcademicTranscript> academicTranscriptDB = academicTranscriptRepository.findByStudent(student);
        Map<String, AcademicTranscript> academicTranscriptHashMap = new HashMap<>();
        for (AcademicTranscript academicTranscript : academicTranscriptDB) {
            String key = academicTranscript.getSubjectCode() + academicTranscript.getTerm();
            academicTranscriptHashMap.put(key, academicTranscript);
        }
        return academicTranscriptHashMap;
    }

    @Transactional
    private void saveStudyAndAttendanceForOneStudent(Student student) {
        List<StudentStudy> studentStudies = new ArrayList<>();
        Map<String, StudentStudy> studentStudyMap = getStudyMap(student);
        boolean isStudyChange = false;

        String url = fapServerUrl + "/api/students/" + "study/all-semester/" + student.getStudentCode();
        AttendanceDTO[] attendanceDTOS = restTemplate.getForObject(url, AttendanceDTO[].class);
        if (attendanceDTOS != null) {

            for (AttendanceDTO dto : attendanceDTOS) {
                String keyForStudy = dto.getSubjectCode() + dto.getSemesterName();
                StudentStudy studentStudyMapValue = studentStudyMap.get(keyForStudy);
                if (studentStudyMapValue == null) {
                    Semester semester = semesterRepository.findByName(dto.getSemesterName()).orElseGet(() -> {
                        // Tạo mới Semester nếu không tìm thấy
                        Semester newSemester = Semester.builder()
                                .name(dto.getSemesterName())
                                .build();
                        return semesterRepository.save(newSemester); // Lưu và trả về bản ghi mới
                    });
                    StudentStudy studentStudy = StudentStudyMapper.toStudentStudy(dto, student, semester);
                    studentStudies.add(studentStudy);
                    isStudyChange = true;
                } else {
                    if(!studentStudyMapValue.getFinalGrade().equals(dto.getGrade())) {
                        studentStudyMapValue.setFinalGrade(dto.getGrade());
                        isStudyChange = true;
                    }
                    if(!studentStudyMapValue.getStatus().equals(StudentStudy.StudyStatus.valueOf(dto.getStatus().name()))) {
                        studentStudyMapValue.setStatus(StudentStudy.StudyStatus.valueOf(dto.getStatus().name()));
                        isStudyChange = true;
                    }
                    if(isStudyChange) {
                        studentStudies.add(studentStudyMapValue);
                    }
                }
            }

            if (isStudyChange) {
                batchSaveStudentStudy(studentStudies);
                studentStudyMap = getStudyMap(student);
            }

            Map<String, AttendanceDetail> studentAttendanceDetailMap = getAttendanceDetailMap(student);
            List<AttendanceDetail> studentAttendanceDetails = new ArrayList<>();
            for (AttendanceDTO dto : attendanceDTOS) {
                String keyForStudy = dto.getStudentCode() + dto.getSemesterName();
                StudentStudy studentStudyMapValue = studentStudyMap.get(keyForStudy);
                if (studentStudyMapValue != null) {
                    for (AttendanceDetailDTO detailDTO : dto.getDetais()) {
                        String keyForDetail = studentStudyMapValue.getSubjectCode() + studentStudyMapValue.getSemester().getName() + detailDTO.getDate().toString() + detailDTO.getSlot();
                        AttendanceDetail studentAttendanceDetailMapValue = studentAttendanceDetailMap.get(keyForDetail);
                        if (studentAttendanceDetailMapValue != null) {
                            boolean isChange = false;
                            if(!studentAttendanceDetailMapValue.getLecturerComment().equals(detailDTO.getLecturerComment())) {
                                studentAttendanceDetailMapValue.setLecturerComment(detailDTO.getLecturerComment());
                                isChange = true;
                            }
                            if(!studentAttendanceDetailMapValue.getStatus().equals(AttendanceStatus.valueOf(detailDTO.getStatus().name()))) {
                                studentAttendanceDetailMapValue.setStatus(AttendanceStatus.valueOf(detailDTO.getStatus().name()));
                                isChange = true;
                            }
                            if(isChange) {
                                studentAttendanceDetails.add(studentAttendanceDetailMapValue);
                            }
                        } else {
                            AttendanceDetail studentAttendanceDetail = StudentStudyMapper.mapAttendanceDetail(detailDTO, studentStudyMapValue);
                            studentAttendanceDetails.add(studentAttendanceDetail);
                        }
                    }

                    if (!studentAttendanceDetails.isEmpty()) {
                        batchSaveStudentAttendanceDetails(studentAttendanceDetails);
                    }
                }
            }

            if (!studentStudies.isEmpty()) {
                batchSaveStudentStudy(studentStudies);
            }

            if (!studentAttendanceDetails.isEmpty()) {
                batchSaveStudentAttendanceDetails(studentAttendanceDetails);
            }
        }
    }

    private void batchSaveStudentStudy(List<StudentStudy> studentStudies) {
        // Chia list thành các batch nhỏ và lưu từng batch
        for (int i = 0; i < studentStudies.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, studentStudies.size()); // Tính toán index cuối cùng của batch
            List<StudentStudy> batch = studentStudies.subList(i, endIndex);
            studentStudyRepository.saveAll(batch);  // Lưu batch

            // Flush và clear sau mỗi batch để giải phóng bộ nhớ
            studentStudyRepository.flush();
        }
        // Sau khi lưu tất cả dữ liệu, clear danh sách để chuẩn bị cho sinh viên tiếp theo
        studentStudies.clear();
    }

    private void batchSaveAcademicTranscript(List<AcademicTranscript> academicTranscripts) {
        // Chia list thành các batch nhỏ và lưu từng batch
        for (int i = 0; i < academicTranscripts.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, academicTranscripts.size()); // Tính toán index cuối cùng của batch
            List<AcademicTranscript> batch = academicTranscripts.subList(i, endIndex);
            academicTranscriptRepository.saveAll(batch);  // Lưu batch

            // Flush và clear sau mỗi batch để giải phóng bộ nhớ
            academicTranscriptRepository.flush();
        }
        // Sau khi lưu tất cả dữ liệu, clear danh sách để chuẩn bị cho sinh viên tiếp theo
        academicTranscripts.clear();
    }

    private void batchSaveStudentAttendanceDetails(List<AttendanceDetail> studentAttendanceDetails) {
        // Chia list thành các batch nhỏ và lưu từng batch
        for (int i = 0; i < studentAttendanceDetails.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, studentAttendanceDetails.size()); // Tính toán index cuối cùng của batch
            List<AttendanceDetail> batch = studentAttendanceDetails.subList(i, endIndex);
            attendanceDetailRepository.saveAll(batch);  // Lưu batch

            // Flush và clear sau mỗi batch để giải phóng bộ nhớ
            attendanceDetailRepository.flush();
        }
        // Sau khi lưu tất cả dữ liệu, clear danh sách để chuẩn bị cho sinh viên tiếp theo
        studentAttendanceDetails.clear();
    }

    public void syncAcademicField() {
        // Gọi API để lấy danh sách DepartmentFapResponseDTO
        ResponseEntity<DepartmentFapResponseDTO[]> response = restTemplate.getForEntity(fapServerUrl + "/api/academic/departments", DepartmentFapResponseDTO[].class);
        DepartmentFapResponseDTO[] departmentDTOs = response.getBody();

        if (departmentDTOs != null) {
            for (DepartmentFapResponseDTO departmentDTO : departmentDTOs) {
                Department department = departmentRepository.findByCode(departmentDTO.getCode())
                        .orElseGet(() -> {
                            // Tạo và lưu Department
                            Department savedDepartment = Department.builder()
                                    .name(departmentDTO.getName())
                                    .code(departmentDTO.getCode())
                                    .build();
                            return departmentRepository.save(savedDepartment);
                        });

                if (!department.getName().equals(departmentDTO.getName())) {
                    department.setName(departmentDTO.getName());
                    departmentRepository.save(department);
                }
                // Tạo và lưu Department
//                Department department = Department.builder()
//                        .name(departmentDTO.getName())
//                        .code(departmentDTO.getCode())
//                        .build();
//                departmentRepository.save(department);

                // Tạo và lưu Major và Specialization cho mỗi Department
                for (MajorFapResponseDTO majorDTO : departmentDTO.getMajors()) {
                    Major major = majorRepository.findByCode(majorDTO.getCode())
                            .orElseGet(() -> {
                                Major saveMajor = Major.builder()
                                        .name(majorDTO.getName())
                                        .code(majorDTO.getCode())
                                        .department(department)
                                        .build();
                                return majorRepository.save(saveMajor);
                            });

                    if (major.getName().equals(majorDTO.getName())) {
                        major.setName(majorDTO.getName());
                        majorRepository.save(major);
                    }
//                    Major major = Major.builder()
//                            .name(majorDTO.getName())
//                            .code(majorDTO.getCode())
//                            .department(department)
//                            .build();
//                    majorRepository.save(major);

                    for (SpecializationFapResponseDTO specializationDTO : majorDTO.getSpecializations()) {
                        Specialization specialization = specializationRepository.findByCode(specializationDTO.getCode())
                                .orElseGet(() -> {
                                    Specialization saveSpecialization = Specialization.builder()
                                            .name(specializationDTO.getName())
                                            .code(specializationDTO.getCode())
                                            .major(major)
                                            .build();
                                    return specializationRepository.save(saveSpecialization);
                                });

                        if (!specialization.getName().equals(specializationDTO.getName())) {
                            specialization.setName(specializationDTO.getName());
                            specializationRepository.save(specialization);
                        }
//                        Specialization specialization = Specialization.builder()
//                                .name(specializationDTO.getName())
//                                .code(specializationDTO.getCode())
//                                .major(major)
//                                .build();
//                        specializationRepository.save(specialization);
                    }
                }
            }
        }
    }

    public void getYesterdayDetailsWithCommentsAndSaveTag() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<AttendanceDetail> attendanceDetails = attendanceDetailRepository.findAttendanceDetailsWithComments(yesterday);
        for (AttendanceDetail attendanceDetail : attendanceDetails) {
            List<String> tagList = parseTagList(attendanceDetail.getLecturerComment());
            if (tagList != null && !tagList.isEmpty()) {
                for(String tag : tagList) {
                    Optional<ProblemTag> problemTag = problemTagRepository.findByName(tag);
                    Semester semester = attendanceDetail.getStudentStudy().getSemester();
                    if (problemTag.isEmpty()) {
                        continue;
                    }
                    StudentStudy studentStudy = attendanceDetail.getStudentStudy();
                    demandProblemTagRepository.save(DemandProblemTag.builder()
                            .problemTag(problemTag.get())
                            .source(studentStudy.getSubjectCode() + String.format("(%s)", studentStudy.getSubjectName()))
                            .semester(semester)
                            .student(studentStudy.getStudent())
                            .isExcluded(false)
                            .build());

                    Optional<StudentFollowing> optionalFollowing = studentFollowingRepository.findByStudentId(studentStudy.getStudent().getId());
                    if (optionalFollowing.isPresent()) {
                        Student student = studentStudy.getStudent();
                        notificationService.sendNotification(NotificationDTO.builder()
                                .receiverId(optionalFollowing.get().getSupportStaff().getId())
                                .message("Student named -" + student.getFullName() + "-" + student.getStudentCode() + String.format("- has new bad comment from teacher on subject %s - %s\n", attendanceDetail.getStudentStudy().getSubjectCode(), attendanceDetail.getStudentStudy().getSubjectName())
                                        + StringUtil.formatTagsWithDate(tagList,attendanceDetail.getDate()))
                                .title("New bad comment for student")
                                .sender("Sync System")
                                .readStatus(false)
                                .build());
                    }
                }
            }
        }
    }

    public List<String> parseTagList(String input) {
        if (isNullOrIsEmptyString(input)) {
            return null;
        }
        String openAICommand = openAIService.generatePromptToOpenAIForParseBehaviorTag(input);
        String tagListInStringRs = openAIService.callOpenAPIForParseBehaviorTag(openAICommand);
        if (isNullOrIsEmptyString(tagListInStringRs)) {
            return null;
        }
        return Arrays.asList(tagListInStringRs.split(",\\s*"));
    }

    private boolean isNullOrIsEmptyString(String input) {
        if (input == null) {
            return true;
        }
        return input.isEmpty();
    }
}

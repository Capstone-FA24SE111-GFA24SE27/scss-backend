package com.capstone2024.scss.infrastructure.data;

import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.student.dto.AttendanceDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.account.entities.Profile;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.account.enums.Status;
import com.capstone2024.scss.domain.common.entity.Semester;
import com.capstone2024.scss.domain.common.mapper.student.academic.StudentStudyMapper;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributedQuestionCardCategory;
import com.capstone2024.scss.domain.contribution_question_card.services.ContributionQuestionCardService;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.AppointmentFeedback;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OfflineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.OnlineAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_report.entities.AppointmentReport;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.CounselingAppointmentRequestStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import com.capstone2024.scss.domain.counselor.entities.*;
import com.capstone2024.scss.domain.common.utils.RandomUtil;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counseling_booking.entities.Holiday;
import com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus;
import com.capstone2024.scss.domain.counselor.entities.enums.Gender;
import com.capstone2024.scss.domain.demand.entities.DemandProblemTag;
import com.capstone2024.scss.domain.demand.entities.ProblemCategory;
import com.capstone2024.scss.domain.demand.entities.ProblemTag;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionCard;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionCardFeedback;
import com.capstone2024.scss.domain.q_and_a.entities.Topic;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionCardStatus;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionType;
import com.capstone2024.scss.domain.q_and_a.enums.TopicType;
import com.capstone2024.scss.domain.student.entities.Department;
import com.capstone2024.scss.domain.student.entities.Major;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.notification.entities.Notification;
import com.capstone2024.scss.domain.student.entities.StudentCounselingProfile;
import com.capstone2024.scss.domain.student.entities.academic.StudentStudy;
import com.capstone2024.scss.domain.student.enums.CounselingProfileStatus;
import com.capstone2024.scss.domain.support_staff.entity.SupportStaff;
import com.capstone2024.scss.infrastructure.configuration.application.StudentSynchronizeService;
import com.capstone2024.scss.infrastructure.configuration.openai.OpenAIService;
import com.capstone2024.scss.infrastructure.data.fap.dto.*;
import com.capstone2024.scss.infrastructure.elastic_search.documents.QuestionCardDocument;
import com.capstone2024.scss.infrastructure.elastic_search.repository.QuestionCardDocumentRepository;
import com.capstone2024.scss.infrastructure.repositories.*;
import com.capstone2024.scss.infrastructure.repositories._and_a.QuestionCardFeedbackRepository;
import com.capstone2024.scss.infrastructure.repositories._and_a.QuestionCardRepository;
import com.capstone2024.scss.infrastructure.repositories._and_a.TopicRepository;
import com.capstone2024.scss.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.scss.infrastructure.repositories.account.LoginTypeRepository;
import com.capstone2024.scss.infrastructure.repositories.account.ProfileRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.AppointmentFeedbackRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingAppointmentRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingAppointmentRequestRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingSlotRepository;
import com.capstone2024.scss.infrastructure.repositories.contribution_question_card.ContributedQuestionCardCategoryRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.*;
import com.capstone2024.scss.infrastructure.repositories.demand.*;
import com.capstone2024.scss.infrastructure.repositories.student.CounselingProfileRepository;
import com.capstone2024.scss.infrastructure.repositories.student.StudentRepository;
import com.capstone2024.scss.infrastructure.repositories.student.academic.StudentStudyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
    private final RestTemplate restTemplate;
    @Value("${server.api.fap.system.base.url}")
    private String fapServerUrl;

    @Value("${hibernate.jdbc.batch_size}")
    private Integer BATCH_SIZE;

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
    private final SlotOfCounselorRepository slotOfCounselorRepository;
    private final QualificationRepository qualificationRepository;
    private final CertificationRepository certificationRepository;
    private final StudentStudyRepository studentStudyRepository;
    private final QuestionCardFeedbackRepository questionCardFeedbackRepository;

    @Override
    public void run(String... args) throws Exception {
        if(studentRepository.count() == 0) {
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
//            seedStudentStudy();
            seedStudentProblemTags();

            generatePromptToOpenAI("Học sinh này học quá tệ, không thường xuyên hỗ trợ làm bài tập nhóm, thường xuyên ngủ gật");

            seedCategories();
            seedQuestionsAndAttachWithCategory();

            seedStudentStudy();
            seedAllQuestionCards();
            seedAppointment();
        } else {
//            testGetYesterday();
        }
    }

    private void testGetYesterday() {
        studentSynchronizeService.getYesterdayDetailsWithCommentsAndSaveTag();
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

        List<DemandProblemTag> demandProblemTagsBatch = new ArrayList<>();

        int index = 0;

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
//                    demandProblemTagRepository.save(DemandProblemTag.builder()
////                                    .problemTag(problemTag.get())
////                                    .source(dto.getSource())
////                                    .semester(semester.get())
////                                    .student(student)
////                                    .isExcluded(semester.get().getName().equals("Summer2024") || !(tagMap.get(dto.getSemesterName() + dto.getSource()) != null && tagMap.get(dto.getSemesterName() + dto.getSource()) > 1))
////                            .build());
                    demandProblemTagsBatch.add(DemandProblemTag.builder()
                                    .problemTag(problemTag.get())
                                    .source(dto.getSource())
                                    .semester(semester.get())
                                    .student(student)
                                    .isExcluded(((index > 4 && semester.get().getName().equals("Fall2024")) || semester.get().getName().equals("Summer2024") || !(tagMap.get(dto.getSemesterName() + dto.getSource()) != null && tagMap.get(dto.getSemesterName() + dto.getSource()) > 1)))
                            .build());

                    if (demandProblemTagsBatch.size() >= 100) {
                        demandProblemTagRepository.saveAll(demandProblemTagsBatch);
                        demandProblemTagRepository.flush();
                        demandProblemTagsBatch.clear();
                    }
                }

                if(!demandProblemTagsBatch.isEmpty()) {
                    demandProblemTagRepository.saveAll(demandProblemTagsBatch);
                    demandProblemTagRepository.flush();
                    demandProblemTagsBatch.clear();
                }
            }
            index++;
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

//    @Transactional
//    public void seedProblemTags() {
//        if (problemTagRepository.count() > 0) {
//            return; // If data already exists, do not seed again
//        }
//
//        // Create or retrieve the "Study" category and other categories
//        ProblemCategory studyCategory = problemCategoryRepository.findByName("Study")
//                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Study").build()));
//
//        problemCategoryRepository.findByName("Extracurricular Activities")
//                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Extracurricular Activities").build()));
//
//        problemCategoryRepository.findByName("Event")
//                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Event").build()));
//
//        problemCategoryRepository.findByName("Club")
//                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Club").build()));
//
//        // Only create tags for the "Study" category
//        List<ProblemTag> studyTags = List.of(
//                // Negative tags
//                ProblemTag.builder().name("Lacks Study Motivation").category(studyCategory).build(),
//                ProblemTag.builder().name("Uncooperative Attitude").category(studyCategory).build(),
//                ProblemTag.builder().name("Low Participation in Class Activities").category(studyCategory).build(),
//                ProblemTag.builder().name("Difficult to Work with in Teams").category(studyCategory).build(),
//                ProblemTag.builder().name("Does Not Support Team Members").category(studyCategory).build(),
//                ProblemTag.builder().name("Poor Team Communication").category(studyCategory).build(),
//                ProblemTag.builder().name("Limited Thinking").category(studyCategory).build(),
//                ProblemTag.builder().name("Slow in Problem Solving").category(studyCategory).build(),
//                ProblemTag.builder().name("Lacks Time Management Skills").category(studyCategory).build(),
//                ProblemTag.builder().name("Not Proactive in Learning").category(studyCategory).build(),
//                ProblemTag.builder().name("Submits Late").category(studyCategory).build(),
//                ProblemTag.builder().name("Does Not Complete Tasks").category(studyCategory).build(),
//                ProblemTag.builder().name("No Improvement").category(studyCategory).build()
//        );
//
//        // Save tags for "Study" to the database
//        problemTagRepository.saveAll(studyTags);
//    }

    @Transactional
    public void seedProblemTags() {
        if (problemTagRepository.count() > 0) {
            return; // If data already exists, do not seed again
        }

        // Create or retrieve categories
        ProblemCategory attentionBehaviorCategory = problemCategoryRepository.findByName("Attention Behavior")
                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Attention Behavior").build()));

        ProblemCategory nonComplianceBehaviorCategory = problemCategoryRepository.findByName("Non-Compliance Behavior")
                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Non-Compliance Behavior").build()));

        ProblemCategory lazinessBehaviorCategory = problemCategoryRepository.findByName("Laziness Behavior")
                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Laziness Behavior").build()));

        ProblemCategory disruptiveBehaviorCategory = problemCategoryRepository.findByName("Disruptive Behavior")
                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Disruptive Behavior").build()));

        ProblemCategory irresponsibleBehaviorCategory = problemCategoryRepository.findByName("Irresponsible Behavior")
                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Irresponsible Behavior").build()));

        ProblemCategory teamCollaborationCategory = problemCategoryRepository.findByName("Team Collaboration")
                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Team Collaboration").build()));

        ProblemCategory respectBehaviorCategory = problemCategoryRepository.findByName("Respect Behavior")
                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Respect Behavior").build()));

        ProblemCategory mentalHealthBehaviorCategory = problemCategoryRepository.findByName("Mental Health Behavior")
                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Mental Health Behavior").build()));

        ProblemCategory communicationSkillCategory = problemCategoryRepository.findByName("Communication Skill")
                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Communication Skill").build()));

        ProblemCategory timeManagementCategory = problemCategoryRepository.findByName("Time Management")
                .orElseGet(() -> problemCategoryRepository.save(ProblemCategory.builder().name("Time Management").build()));

        // Create tags for the categories
        List<ProblemTag> attentionBehaviorTags = List.of(
                ProblemTag.builder().name("Easily Distracted by Surroundings").category(attentionBehaviorCategory).build(),
                ProblemTag.builder().name("Does Not Follow Lecture").category(attentionBehaviorCategory).build(),
                ProblemTag.builder().name("Frequent Mental Distractions").category(attentionBehaviorCategory).build(),
                ProblemTag.builder().name("Lack of Eye Contact").category(attentionBehaviorCategory).build(),
                ProblemTag.builder().name("Constantly Looking Around").category(attentionBehaviorCategory).build(),
                ProblemTag.builder().name("Not Engaged in the Discussion").category(attentionBehaviorCategory).build(),
                ProblemTag.builder().name("Attention Wanders During Lectures").category(attentionBehaviorCategory).build(),
                ProblemTag.builder().name("Gazing Outside").category(attentionBehaviorCategory).build()
        );

        List<ProblemTag> nonComplianceBehaviorTags = List.of(
                ProblemTag.builder().name("Frequent Tardiness").category(nonComplianceBehaviorCategory).build(),
                ProblemTag.builder().name("Skipping Mandatory Classes").category(nonComplianceBehaviorCategory).build(),
                ProblemTag.builder().name("Skipping Exams or Quizzes").category(nonComplianceBehaviorCategory).build(),
                ProblemTag.builder().name("Repeatedly Ignoring Attendance Policy").category(nonComplianceBehaviorCategory).build(),
                ProblemTag.builder().name("Violating Computer Usage Policy").category(nonComplianceBehaviorCategory).build(),
                ProblemTag.builder().name("Unauthorized Use of Mobile in Class").category(nonComplianceBehaviorCategory).build(),
                ProblemTag.builder().name("Breaking Dress Code").category(nonComplianceBehaviorCategory).build(),
                ProblemTag.builder().name("Violating Lab Safety Rules").category(nonComplianceBehaviorCategory).build(),
                ProblemTag.builder().name("Leaving Class Early").category(nonComplianceBehaviorCategory).build(),
                ProblemTag.builder().name("Frequent Late Submissions").category(nonComplianceBehaviorCategory).build()
        );

        List<ProblemTag> lazinessBehaviorTags = List.of(
                ProblemTag.builder().name("Refuses to Answer Questions").category(lazinessBehaviorCategory).build(),
                ProblemTag.builder().name("Avoiding Group Discussions").category(lazinessBehaviorCategory).build(),
                ProblemTag.builder().name("Not Participating in Group Assignments").category(lazinessBehaviorCategory).build(),
                ProblemTag.builder().name("Ignoring Class Activities").category(lazinessBehaviorCategory).build(),
                ProblemTag.builder().name("Does Not Initiate Work").category(lazinessBehaviorCategory).build(),
                ProblemTag.builder().name("Procrastinating on Assignments").category(lazinessBehaviorCategory).build(),
                ProblemTag.builder().name("Lack of Initiative").category(lazinessBehaviorCategory).build(),
                ProblemTag.builder().name("Submits Only the Bare Minimum").category(lazinessBehaviorCategory).build(),
                ProblemTag.builder().name("Doing the Bare Minimum in Group Work").category(lazinessBehaviorCategory).build(),
                ProblemTag.builder().name("No Contribution to Group Work").category(lazinessBehaviorCategory).build()
        );

        List<ProblemTag> disruptiveBehaviorTags = List.of(
                ProblemTag.builder().name("Interrupting Teacher’s Explanation").category(disruptiveBehaviorCategory).build(),
                ProblemTag.builder().name("Talking During Lecture").category(disruptiveBehaviorCategory).build(),
                ProblemTag.builder().name("Distracting Others").category(disruptiveBehaviorCategory).build(),
                ProblemTag.builder().name("Disrupting Classroom Discussions").category(disruptiveBehaviorCategory).build(),
                ProblemTag.builder().name("Starting Arguments with Peers").category(disruptiveBehaviorCategory).build(),
                ProblemTag.builder().name("Inappropriate Behavior in Class").category(disruptiveBehaviorCategory).build(),
                ProblemTag.builder().name("Speaking Loudly").category(disruptiveBehaviorCategory).build(),
                ProblemTag.builder().name("Throwing Objects in Class").category(disruptiveBehaviorCategory).build(),
                ProblemTag.builder().name("Acting Playful During Serious Moments").category(disruptiveBehaviorCategory).build(),
                ProblemTag.builder().name("Disrupting the Flow of the Lesson").category(disruptiveBehaviorCategory).build()
        );

        List<ProblemTag> irresponsibleBehaviorTags = List.of(
                ProblemTag.builder().name("Not Taking Class Seriously").category(irresponsibleBehaviorCategory).build(),
                ProblemTag.builder().name("Not Preparing for Classes").category(irresponsibleBehaviorCategory).build(),
                ProblemTag.builder().name("Neglecting to Complete Assignments").category(irresponsibleBehaviorCategory).build(),
                ProblemTag.builder().name("Delaying Assignment Submissions").category(irresponsibleBehaviorCategory).build(),
                ProblemTag.builder().name("Refuses to Do Homework").category(irresponsibleBehaviorCategory).build(),
                ProblemTag.builder().name("No Effort in Group Work").category(irresponsibleBehaviorCategory).build(),
                ProblemTag.builder().name("Missed Deadlines").category(irresponsibleBehaviorCategory).build(),
                ProblemTag.builder().name("Minimal Effort in Project Work").category(irresponsibleBehaviorCategory).build(),
                ProblemTag.builder().name("Not Trying to Improve").category(irresponsibleBehaviorCategory).build(),
                ProblemTag.builder().name("Does Not Seek Clarification").category(irresponsibleBehaviorCategory).build()
        );

        List<ProblemTag> teamCollaborationTags = List.of(
                ProblemTag.builder().name("Lack of Communication in Team").category(teamCollaborationCategory).build(),
                ProblemTag.builder().name("Not Sharing Ideas with Team").category(teamCollaborationCategory).build(),
                ProblemTag.builder().name("Not Listening to Team Members").category(teamCollaborationCategory).build(),
                ProblemTag.builder().name("Avoiding Team Work").category(teamCollaborationCategory).build(),
                ProblemTag.builder().name("Does Not Contribute in Team Meetings").category(teamCollaborationCategory).build(),
                ProblemTag.builder().name("Lack of Team Spirit").category(teamCollaborationCategory).build(),
                ProblemTag.builder().name("Refuses to Collaborate on Projects").category(teamCollaborationCategory).build(),
                ProblemTag.builder().name("Undermines Team Decisions").category(teamCollaborationCategory).build(),
                ProblemTag.builder().name("Does Not Support Team Members").category(teamCollaborationCategory).build(),
                ProblemTag.builder().name("Does Not Help When Required").category(teamCollaborationCategory).build()
        );

        List<ProblemTag> respectBehaviorTags = List.of(
                ProblemTag.builder().name("Interrupting Others in Discussions").category(respectBehaviorCategory).build(),
                ProblemTag.builder().name("Not Acknowledging Peers’ Ideas").category(respectBehaviorCategory).build(),
                ProblemTag.builder().name("Disrespecting Teacher’s Authority").category(respectBehaviorCategory).build(),
                ProblemTag.builder().name("Not Following Classroom Etiquette").category(respectBehaviorCategory).build(),
                ProblemTag.builder().name("Talking Over Others").category(respectBehaviorCategory).build(),
                ProblemTag.builder().name("Ignoring Classroom Norms").category(respectBehaviorCategory).build(),
                ProblemTag.builder().name("Being Disrespectful to Peers").category(respectBehaviorCategory).build(),
                ProblemTag.builder().name("Making Disparaging Remarks").category(respectBehaviorCategory).build(),
                ProblemTag.builder().name("Disrespecting Cultural Sensitivities").category(respectBehaviorCategory).build(),
                ProblemTag.builder().name("Using Offensive Language").category(respectBehaviorCategory).build()
        );

        List<ProblemTag> mentalHealthBehaviorTags = List.of(
                ProblemTag.builder().name("Frequent Mood Swings").category(mentalHealthBehaviorCategory).build(),
                ProblemTag.builder().name("Signs of Anxiety").category(mentalHealthBehaviorCategory).build(),
                ProblemTag.builder().name("Struggles with Stress Management").category(mentalHealthBehaviorCategory).build(),
                ProblemTag.builder().name("Withdrawing from Others").category(mentalHealthBehaviorCategory).build(),
                ProblemTag.builder().name("Lack of Motivation").category(mentalHealthBehaviorCategory).build(),
                ProblemTag.builder().name("Tiredness and Fatigue").category(mentalHealthBehaviorCategory).build(),
                ProblemTag.builder().name("Frequent Absences Due to Mental Health").category(mentalHealthBehaviorCategory).build(),
                ProblemTag.builder().name("Frequent Crying Episodes").category(mentalHealthBehaviorCategory).build(),
                ProblemTag.builder().name("Experiencing Panic Attacks").category(mentalHealthBehaviorCategory).build(),
                ProblemTag.builder().name("Increased Irritability").category(mentalHealthBehaviorCategory).build()
        );

        List<ProblemTag> communicationSkillTags = List.of(
                ProblemTag.builder().name("Difficulty Expressing Ideas Clearly").category(communicationSkillCategory).build(),
                ProblemTag.builder().name("Struggles with Public Speaking").category(communicationSkillCategory).build(),
                ProblemTag.builder().name("Not Asking for Help When Needed").category(communicationSkillCategory).build(),
                ProblemTag.builder().name("Limited Vocabulary").category(communicationSkillCategory).build(),
                ProblemTag.builder().name("Uses Excessive Jargon").category(communicationSkillCategory).build(),
                ProblemTag.builder().name("Difficulty with Written Communication").category(communicationSkillCategory).build(),
                ProblemTag.builder().name("Inability to Communicate Effectively in Group Settings").category(communicationSkillCategory).build(),
                ProblemTag.builder().name("Difficulty Understanding Feedback").category(communicationSkillCategory).build(),
                ProblemTag.builder().name("Inconsistent Tone of Voice").category(communicationSkillCategory).build(),
                ProblemTag.builder().name("Frequent Misunderstandings in Communication").category(communicationSkillCategory).build()
        );

        List<ProblemTag> timeManagementTags = List.of(
                ProblemTag.builder().name("Procrastination on Deadlines").category(timeManagementCategory).build(),
                ProblemTag.builder().name("Overcommitting to Tasks").category(timeManagementCategory).build(),
                ProblemTag.builder().name("Mismanaging Time During Projects").category(timeManagementCategory).build(),
                ProblemTag.builder().name("Not Prioritizing Work Effectively").category(timeManagementCategory).build(),
                ProblemTag.builder().name("Failing to Meet Deadlines").category(timeManagementCategory).build(),
                ProblemTag.builder().name("Leaving Work Until the Last Minute").category(timeManagementCategory).build(),
                ProblemTag.builder().name("Wasting Time on Unimportant Tasks").category(timeManagementCategory).build(),
                ProblemTag.builder().name("Difficulty Estimating Time Needed for Tasks").category(timeManagementCategory).build(),
                ProblemTag.builder().name("Avoiding Time-Sensitive Tasks").category(timeManagementCategory).build(),
                ProblemTag.builder().name("Difficulty Balancing Multiple Tasks").category(timeManagementCategory).build()
        );

        // Save all the tags to the repository
        problemTagRepository.saveAll(attentionBehaviorTags);
        problemTagRepository.saveAll(nonComplianceBehaviorTags);
        problemTagRepository.saveAll(lazinessBehaviorTags);
        problemTagRepository.saveAll(disruptiveBehaviorTags);
        problemTagRepository.saveAll(irresponsibleBehaviorTags);
        problemTagRepository.saveAll(teamCollaborationTags);
        problemTagRepository.saveAll(respectBehaviorTags);
        problemTagRepository.saveAll(mentalHealthBehaviorTags);
        problemTagRepository.saveAll(communicationSkillTags);
        problemTagRepository.saveAll(timeManagementTags);
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
        String adminEmail = "admin@gmail.com";
        logger.info("Checking if admin account with email '{}' exists.", adminEmail);

        if (accountRepository.findAccountByEmail(adminEmail).isEmpty()) {
            logger.info("Admin account does not exist. Creating new admin account.");

            Account admin = Account.builder()
                    .email(adminEmail)
                    .role(Role.ADMINISTRATOR)
                    .status(Status.ACTIVE)
                    .password(passwordEncoder.encode("112233"))
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
                    .password(passwordEncoder.encode("112233"))
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
//            if (createCounselingProfile) {
//                StudentCounselingProfile counselingProfile = StudentCounselingProfile.builder()
//                        .student(studentProfile)
//                        .introduction("Introduction")
//                        .currentHealthStatus("Healthy")
//                        .psychologicalStatus("Stable")
//                        .stressFactors("Low stress")
//                        .academicDifficulties("None")
//                        .studyPlan("Plan to excel in studies")
//                        .careerGoals("Become a Software Engineer")
//                        .partTimeExperience("Intern at Tech Company")
//                        .internshipProgram("Summer Internship 2024")
//                        .extracurricularActivities("Football Club")
//                        .personalInterests("Reading, Traveling")
//                        .socialRelationships("Good relationships with peers")
//                        .financialSituation("Stable")
//                        .financialSupport("Parents")
//                        .desiredCounselingFields("Career Counseling, Mental Health")
//                        .status(CounselingProfileStatus.VERIFIED)
//                        .build();
//
//                studentProfile.setCounselingProfile(counselingProfile);
//            }

            logger.info("Student account created with email '{}'.", studentEmail);
            return profileRepository.save(studentProfile);
        } else {
            logger.warn("Student account with email '{}' already exists.", studentEmail);
        }
        return null;
    }

    private void seedManagerAccount() {
        String managerEmail = "manager@gmail.com";
        logger.info("Checking if manager account with email '{}' exists.", managerEmail);

        if (accountRepository.findAccountByEmail(managerEmail).isEmpty()) {
            logger.info("Manager account does not exist. Creating new manager account.");

            Account manager = Account.builder()
                    .email(managerEmail)
                    .role(Role.MANAGER)
                    .password(passwordEncoder.encode("112233"))
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
        String supportStaffEmail = "ss@gmail.com";
        logger.info("Checking if support staff account with email '{}' exists.", supportStaffEmail);

        if (accountRepository.findAccountByEmail(supportStaffEmail).isEmpty()) {
            logger.info("Support staff account does not exist. Creating new support staff account.");

            Account supportStaff = Account.builder()
                    .email(supportStaffEmail)
                    .role(Role.SUPPORT_STAFF)  // Assuming you have a SUPPORT_STAFF role
                    .status(Status.ACTIVE)
                    .password(passwordEncoder.encode("112233"))
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
    private void createSchoolPsychologists(List<Expertise> expertiseList, List<CounselingSlot> counselingSlots) {
        // Tìm expertise 'School Psychologist'
        Expertise schoolPsychologist = expertiseList.stream()
                .filter(expertise -> "School Psychologist".equals(expertise.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expertise 'School Psychologist' not found"));

        // Tạo Female counselor - Nguyễn Thị Tâm
        Counselor counselor1 = createNonAcademicCounselor(0, Gender.FEMALE, "Nguyễn Thị Tâm", schoolPsychologist, counselingSlots, "tamnnt");
        counselor1 = counselorRepository.findById(counselor1.getId()).get();

        List<Qualification> qualificationsOfCounselor1 = new ArrayList<>();
        qualificationsOfCounselor1.add(Qualification.builder()
                .degree("Bachelor")
                .institution("Hanoi National University of Education")
                .fieldOfStudy("Education Psychology")
                .imageUrl("https://static.dav.edu.vn/w800/images/upload/2021/07/17/anh-1.jpg")
                .counselor(counselor1)
                .build());
        qualificationsOfCounselor1.add(Qualification.builder()
                .degree("Master")
                .institution("University of Melbourne, Australia")
                .fieldOfStudy("Clinical Psychology")
                .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQQGnqM0i8fCPTDtDiujUCapjHWfPQplB0F_Q&s")
                .counselor(counselor1)
                .build());

        List<Certification> certificationsOfCounselor1 = new ArrayList<>();
        certificationsOfCounselor1.add(Certification.builder()
                .name("Licensed School Psychologist")
                .organization("International Association of School Psychologists (IASP)")
                .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOeknksixKIigzdkxJ6H6AmYGbj3RljHaYdw&s")
                .counselor(counselor1)
                .build());
        certificationsOfCounselor1.add(Certification.builder()
                .name("Certified Trauma-Informed Care Specialist")
                .organization("Trauma Institute International")
                .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSQsyfuR0lVMotTKBnT4O5LEMr7ooU478KK0A&s")
                .counselor(counselor1)
                .build());

        counselor1.setAchievements("Designed and implemented a peer mentoring program,\n"
                + "increased student academic performance by 15%.\n"
                + "Presented research on the impact of mental health support on student outcomes\n"
                + "at the International Education Psychology Conference 2023.");
        counselor1.setWorkHistory("School Psychologist at ABC International University (2019–Present):\n"
                + "- Provided counseling and psychological assessments for over 200 students annually.\n"
                + "- Collaborated with academic staff to address students' learning challenges.\n\n"
                + "Intern Psychologist at XYZ High School (2018–2019):\n"
                + "- Conducted group counseling sessions on stress management.\n"
                + "- Developed behavior intervention plans for at-risk students.");
        counselor1.setSpecializedSkills("Psychological assessment\n"
                + "Behavioral intervention\n"
                + "Crisis management\n"
                + "Development of intervention strategies.");
        counselor1.setOtherSkills("Public speaking\n"
                + "Report writing\n"
                + "Fluent in Vietnamese and English\n"
                + "Team collaboration and leadership.");
        counselor1.setQualifications(qualificationsOfCounselor1);
        counselor1.setCertifications(certificationsOfCounselor1);

        // Tạo Male counselor - Nguyễn Văn An
        Counselor counselor3 = createNonAcademicCounselor(0, Gender.MALE, "Nguyễn Văn An", schoolPsychologist, counselingSlots, "anndv");
        counselor3 = counselorRepository.findById(counselor3.getId()).get();

        List<Qualification> qualificationsOfCounselor3 = new ArrayList<>();
        qualificationsOfCounselor3.add(Qualification.builder()
                .degree("Bachelor")
                .institution("University of Social Sciences and Humanities")
                .fieldOfStudy("Psychology")
                .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR49C_D_JnMDYMnNhKARxZ2ACW7_26S1J4waQ&s")
                .counselor(counselor3)
                .build());
        qualificationsOfCounselor3.add(Qualification.builder()
                .degree("Master")
                .institution("University of Education, Vietnam")
                .fieldOfStudy("School Psychology")
                .imageUrl("https://static.dav.edu.vn/images/upload/2021/07/17/anh-1.jpg")
                .counselor(counselor3)
                .build());

        List<Certification> certificationsOfCounselor3 = new ArrayList<>();
        certificationsOfCounselor3.add(Certification.builder()
                .name("Certified Cognitive Behavioral Therapist (CBT)")
                .organization("Beck Institute for Cognitive Behavior Therapy")
                .imageUrl("https://cdn.slidesharecdn.com/ss_thumbnails/272cfba4-b900-4c0a-bf17-e8ac51c7678b-161130003747-thumbnail.jpg?width=640&height=640&fit=bounds")
                .counselor(counselor3)
                .build());
        certificationsOfCounselor3.add(Certification.builder()
                .name("Emotional Intelligence Practitioner Certificate")
                .organization("Six Seconds Emotional Intelligence Network")
                .imageUrl("https://media.licdn.com/dms/image/v2/D4D22AQFsRehUA-l8lw/feedshare-shrink_800/feedshare-shrink_800/0/1704743953659?e=2147483647&v=beta&t=71OCkOrRk09jFn2yScvIkk65J5Xq64hwWsOJ1tJMAEE")
                .counselor(counselor3)
                .build());

        counselor3.setAchievements("Introduced an anti-bullying program,\n"
                + "decreased reported incidents by 40%.\n"
                + "Developed a digital mindfulness toolkit adopted by 15 schools nationwide.");
        counselor3.setWorkHistory("Senior School Psychologist at DEF University (2020–Present):\n"
                + "- Designed and delivered training workshops for school counselors.\n"
                + "- Provided individual therapy for students experiencing anxiety and depression.\n\n"
                + "School Counselor at High Potential Secondary School (2015–2020):\n"
                + "- Conducted psychoeducational testing for learning disabilities.\n"
                + "- Supported staff in implementing classroom behavior management strategies.");
        counselor3.setSpecializedSkills("Trauma-informed therapy\n"
                + "Career counseling\n"
                + "Psychoeducation material design\n"
                + "Conflict resolution.");
        counselor3.setOtherSkills("Leadership\n"
                + "Statistical analysis (SPSS)\n"
                + "Fluent in Vietnamese, English, and conversational French\n"
                + "Workshop facilitation.");
        counselor3.setQualifications(qualificationsOfCounselor3);
        counselor3.setCertifications(certificationsOfCounselor3);

        counselorRepository.save(counselor1);
        counselorRepository.save(counselor3);
//        qualificationRepository.saveAll(qualificationsOfCounselor1);
    }

    @Transactional
    private void createSchoolHealthAdvisors(List<Expertise> expertiseList, List<CounselingSlot> counselingSlots) {
        Expertise schoolHealthAdvisor = expertiseList.stream()
                .filter(expertise -> "School Health Advisor".equals(expertise.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expertise 'School Health Advisor' not found"));

        // Tạo Female counselor - Lê Thị Hạnh
        Counselor counselor1 = createNonAcademicCounselor(0, Gender.FEMALE, "Lê Thị Hạnh", schoolHealthAdvisor, counselingSlots, "hanhltna");
        counselor1 = counselorRepository.findById(counselor1.getId()).get();

        counselor1.setAchievements("Coordinated a health education program reaching 1,000 students,\n"
                + "increasing awareness of healthy living habits by 25%.\n"
                + "Presented at the National School Health Summit 2022 on stress management techniques for students.");
        counselor1.setWorkHistory("School Health Advisor at Green Hills High School (2018–Present):\n"
                + "- Implemented health check-up campaigns for over 500 students annually.\n"
                + "- Provided one-on-one health counseling sessions focusing on nutrition and mental health.\n\n"
                + "Health Educator at Sunshine Academy (2015–2018):\n"
                + "- Organized monthly workshops on hygiene and physical fitness.\n"
                + "- Collaborated with parents to address students' health concerns.");
        counselor1.setSpecializedSkills("Health promotion\n"
                + "Nutrition counseling\n"
                + "Stress management\n"
                + "Health risk assessment.");
        counselor1.setOtherSkills("Public speaking\n"
                + "Team management\n"
                + "Fluent in Vietnamese and English.");
        counselor1.setQualifications(List.of(
                Qualification.builder()
                        .degree("Bachelor")
                        .fieldOfStudy("Health Education")
                        .institution("University of Medicine and Pharmacy, HCMC")
                        .yearOfGraduation(2014)
                        .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTz8a4nehB68ObvzkyxFM50YM4zduGn4rHzxA&s")
                        .counselor(counselor1)
                        .build(),
                Qualification.builder()
                        .degree("Master")
                        .fieldOfStudy("Public Health")
                        .institution("University of Sydney")
                        .yearOfGraduation(2018)
                        .imageUrl("https://media.licdn.com/dms/image/v2/D560BAQHh3czp-_H4Ag/company-logo_200_200/company-logo_200_200/0/1693370213881/sydney_school_of_public_health_the_university_of_sydney_logo?e=2147483647&v=beta&t=2ydJsu0pYQd4qLWJmPsrXASm2S2DVvq-dHehQZu4_q0")
                        .counselor(counselor1)
                        .build()
        ));
        counselor1.setCertifications(List.of(
                Certification.builder()
                        .name("Certified Public Health Specialist")
                        .organization("Global Health Institute")
                        .imageUrl("https://images.squarespace-cdn.com/content/v1/5d8cdbc52d89533d477c7a41/e92ebca8-98af-4004-a187-6885187dd1ff/NDPHTN-GLobal-Health-Certificate-Sample+%281%29.png")
                        .counselor(counselor1)
                        .build(),
                Certification.builder()
                        .name("Certified Nutrition Specialist")
                        .organization("International Society for Sports Nutrition")
                        .imageUrl("https://media.licdn.com/dms/image/v2/D4E22AQFh0Bg0EIkVoA/feedshare-shrink_800/feedshare-shrink_800/0/1681779480133?e=2147483647&v=beta&t=LL7Qkf9Mo3ouCxZIRhtyPBmCky2jQrv9-1ekuiF8ZGI")
                        .counselor(counselor1)
                        .build(),
                Certification.builder()
                        .name("Mental Health First Aid")
                        .organization("Mental Health America")
                        .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQkmScYp23SZ-ncuWxCIAyoAVuNqPSygODGPg&s")
                        .counselor(counselor1)
                        .build()
        ));

        // Tạo Male counselor - Trần Văn Hải
        Counselor counselor2 = createNonAcademicCounselor(0, Gender.MALE, "Trần Văn Hải", schoolHealthAdvisor, counselingSlots, "haitvna");
        counselor2 = counselorRepository.findById(counselor2.getId()).get();

        counselor2.setAchievements("Established a physical fitness program, increasing student participation in sports by 30%.\n"
                + "Recognized by the National Health Association for efforts in combating adolescent obesity.");
        counselor2.setWorkHistory("Senior Health Advisor at Golden Gate High School (2020–Present):\n"
                + "- Conducted fitness assessments for students and staff.\n"
                + "- Organized annual health fairs involving local medical professionals.\n\n"
                + "Health Consultant at ABC Wellness Center (2015–2020):\n"
                + "- Delivered health seminars on stress and time management.\n"
                + "- Counseled teenagers on maintaining a balanced diet.");
        counselor2.setSpecializedSkills("Fitness coaching\n"
                + "Adolescent health\n"
                + "Program development\n"
                + "Health data analysis.");
        counselor2.setOtherSkills("Leadership\n"
                + "Event planning\n"
                + "Fluent in Vietnamese and conversational Japanese.");
        counselor2.setQualifications(List.of(
                Qualification.builder()
                        .degree("Bachelor")
                        .fieldOfStudy("Physical Education")
                        .institution("National Sports University, Vietnam")
                        .yearOfGraduation(2013)
                        .imageUrl("https://static.dav.edu.vn/w640/images/upload/2024/06/03/anh-3.png")
                        .counselor(counselor2)
                        .build(),
                Qualification.builder()
                        .degree("Master")
                        .fieldOfStudy("Health and Wellness")
                        .institution("University of Tokyo")
                        .yearOfGraduation(2018)
                        .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRe-eVNzuBUywzV5Y-SRUVIXBT5Re9z87u7arxwRSlnscrWLIeFxdLokOd0rXGRsfim-BE&usqp=CAU")
                        .counselor(counselor2)
                        .build()
        ));
        counselor2.setCertifications(List.of(
                Certification.builder()
                        .name("Advanced Fitness Trainer Certification")
                        .organization("International Sports Sciences Association (ISSA)")
                        .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQYN1jOm3bx0IKNqhjVDJpHXr_lEuzNubVmrA&s")
                        .counselor(counselor2)
                        .build(),
                Certification.builder()
                        .name("Certified Strength and Conditioning Specialist")
                        .organization("National Strength and Conditioning Association")
                        .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTJKNPHtSO2xbdhlHQxusZWVCx507OzkpZyGA&s")
                        .counselor(counselor2)
                        .build(),
                Certification.builder()
                        .name("Certified Personal Trainer")
                        .organization("American Council on Exercise")
                        .imageUrl("https://www.fitthai.com/wp-content/uploads/2024/02/ace-certificate-1024x724.png")
                        .counselor(counselor2)
                        .build()
        ));

        counselorRepository.save(counselor1);
        counselorRepository.save(counselor2);
    }

    @Transactional
    private void createCareerCounselors(List<Expertise> expertiseList, List<CounselingSlot> counselingSlots) {
        Expertise careerCounseling = expertiseList.stream()
                .filter(expertise -> "Career Counseling".equals(expertise.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expertise 'Career Counseling' not found"));

        // Tạo Female counselor - Phạm Thị Hoa
        Counselor counselor3 = createNonAcademicCounselor(0, Gender.FEMALE, "Phạm Thị Hoa", careerCounseling, counselingSlots, "hoaptna");
        counselor3 = counselorRepository.findById(counselor3.getId()).get();

        counselor3.setAchievements("Guided over 300 students in selecting suitable career paths.\n"
                + "Authored 'The Career Compass' - a guidebook on career planning.");
        counselor3.setWorkHistory("Career Advisor at Vietnam National University (2019–Present):\n"
                + "- Conducted workshops on resume writing and interview skills.\n"
                + "- Mentored students in crafting personalized career strategies.\n\n"
                + "Career Coach at Bright Futures (2015–2019):\n"
                + "- Assisted graduates in transitioning to the workforce.\n"
                + "- Collaborated with recruiters to secure internship opportunities.");
        counselor3.setSpecializedSkills("Career assessment\n"
                + "Soft skills training\n"
                + "Resume and cover letter review\n"
                + "Career path mapping.");
        counselor3.setOtherSkills("Strong communication\n"
                + "Mentoring\n"
                + "Fluent in Vietnamese and English.");
        counselor3.setQualifications(List.of(
                Qualification.builder()
                        .degree("Bachelor")
                        .fieldOfStudy("Human Resources Management")
                        .institution("University of Economics, HCMC")
                        .yearOfGraduation(2013)
                        .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSleaAa2r-hXU7y3_nifgTpieXizPjL6L83tA&s")
                        .counselor(counselor3)
                        .build(),
                Qualification.builder()
                        .degree("Master")
                        .fieldOfStudy("Career Counseling")
                        .institution("University of California")
                        .yearOfGraduation(2017)
                        .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSNAj1NHiDc272E6OxEmqg4PZDvXx8_2tiG_w&s")
                        .counselor(counselor3)
                        .build()
        ));
        counselor3.setCertifications(List.of(
                Certification.builder()
                        .name("Certified Career Coach (CCC)")
                        .organization("International Coach Federation")
                        .imageUrl("https://cdn.slidesharecdn.com/ss_thumbnails/ccccertificate-171206192452-thumbnail.jpg?width=640&height=640&fit=bounds")
                        .counselor(counselor3)
                        .build(),
                Certification.builder()
                        .name("Certified Professional Resume Writer (CPRW)")
                        .organization("Professional Association of Resume Writers and Career Coaches")
                        .imageUrl("https://media.licdn.com/dms/image/v2/C4E22AQHP0jM2McPSNA/feedshare-shrink_2048_1536/feedshare-shrink_2048_1536/0/1662998892557?e=2147483647&v=beta&t=jm7rlssu9PsZDMz4gBtLtui8A0GCDBBeBiijWP_XYsc")
                        .counselor(counselor3)
                        .build()
        ));

        // Tạo Male counselor - Lê Văn Tài
        Counselor counselor4 = createNonAcademicCounselor(0, Gender.MALE, "Lê Văn Tài", careerCounseling, counselingSlots, "tainlva");
        counselor4 = counselorRepository.findById(counselor4.getId()).get();

        counselor4.setAchievements("Designed a career mentorship program, reducing student unemployment by 20%.\n"
                + "Delivered keynote speeches at career fairs nationwide.");
        counselor4.setWorkHistory("Senior Career Consultant at Global Talent Solutions (2018–Present):\n"
                + "- Assisted multinational companies with talent recruitment.\n"
                + "- Provided career development workshops for corporate employees.\n\n"
                + "Career Advisor at National University of Hanoi (2014–2018):\n"
                + "- Helped students with job search strategies and market insights.\n"
                + "- Managed an internship placement program.");
        counselor4.setSpecializedSkills("Job market analysis\n"
                + "Interview coaching\n"
                + "Corporate recruitment\n"
                + "Employee development.");
        counselor4.setOtherSkills("Presentation skills\n"
                + "Conflict resolution\n"
                + "Fluent in Vietnamese, English, and Japanese.");
        counselor4.setQualifications(List.of(
                Qualification.builder()
                        .degree("Bachelor")
                        .fieldOfStudy("Business Administration")
                        .institution("National Economics University, Hanoi")
                        .yearOfGraduation(2012)
                        .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSFhi_1zSHlWeNNtz1XCHdbJpdkgciBJ4QRIg&s")
                        .counselor(counselor4)
                        .build(),
                Qualification.builder()
                        .degree("Master")
                        .fieldOfStudy("Human Resources Management")
                        .institution("University of Melbourne")
                        .yearOfGraduation(2016)
                        .imageUrl("https://www.4icu.org/i/programs-courses-degrees/master-of-human-resource-management-high-resolution.png")
                        .counselor(counselor4)
                        .build()
        ));
        counselor4.setCertifications(List.of(
                Certification.builder()
                        .name("Certified Human Resources Professional (CHRP)")
                        .organization("Human Resources Certification Institute")
                        .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTXe-ibey-6AeN3PCLVOA0uoqPp8q4EO7wv1g&s")
                        .counselor(counselor4)
                        .build(),
                Certification.builder()
                        .name("Certified Professional Career Coach (CPCC)")
                        .organization("Career Development Network")
                        .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQA5VDyO-y0ybIcMROtdrm0XnW2eo_BszktKA&s")
                        .counselor(counselor4)
                        .build(),
                Certification.builder()
                        .name("Global Career Development Facilitator (GCDF)")
                        .organization("Center for Credentialing & Education")
                        .imageUrl("https://image.slidesharecdn.com/gcdfcertificate-171116131910/85/Global-Career-Development-Facilitator-GCDF-Certificate-1-320.jpg")
                        .counselor(counselor4)
                        .build()
        ));

        counselorRepository.save(counselor3);
        counselorRepository.save(counselor4);
    }

    @Transactional
    private void createITDepartmentCounselor(List<CounselingSlot> counselingSlots) {
        Major seMajor = majorRepository
                .findByName("Software Engineering")
                .orElseThrow(() -> new NotFoundException("Not found SE major"));

        // Tạo Female counselor - Trần Thị Thu Hà
        Counselor counselor1 = createAcademicCounselor(0, Gender.FEMALE, "Trần Thị Thu Hà", seMajor, counselingSlots, "hattta");
        counselor1 = counselorRepository.findById(counselor1.getId()).get();

        counselor1.setAchievements("Led a successful internship program for 100+ students in collaboration with top IT companies.\n"
                + "Published a paper on 'The Impact of AI on Software Development' in 2022.");
        counselor1.setWorkHistory("Senior IT Career Counselor at Vietnam National University (2020–Present):\n"
                + "- Mentored over 200 students pursuing careers in software engineering.\n"
                + "- Coordinated with tech companies to provide internship and job opportunities for graduates.\n\n"
                + "Software Development Intern at GlobalTech Inc. (2015–2017):\n"
                + "- Worked on developing web applications using JavaScript and Python.\n"
                + "- Assisted in software testing and debugging.");
        counselor1.setSpecializedSkills("Software development\n"
                + "Internship program management\n"
                + "Career counseling for IT students\n"
                + "Technical interview preparation.");
        counselor1.setOtherSkills("Strong communication and presentation skills\n"
                + "Team leadership\n"
                + "Fluent in Vietnamese and English.");
        counselor1.setQualifications(List.of(
                Qualification.builder()
                        .degree("Bachelor")
                        .fieldOfStudy("Software Engineering")
                        .institution("University of Danang")
                        .yearOfGraduation(2015)
                        .imageUrl("https://brycen.com.vn/wp-content/uploads/2024/04/1706145888028.jpeg")
                        .counselor(counselor1)
                        .build(),
                Qualification.builder()
                        .degree("Master")
                        .fieldOfStudy("Computer Science")
                        .institution("University of California, Berkeley")
                        .yearOfGraduation(2020)
                        .imageUrl("https://media.licdn.com/dms/image/v2/D5622AQErroPql5RTqQ/feedshare-shrink_800/feedshare-shrink_800/0/1695165233848?e=2147483647&v=beta&t=aqz28HJghtdtnUBU9yuHst45m9K5ihCS3EEBIIFn16M")
                        .counselor(counselor1)
                        .build()
        ));
        counselor1.setCertifications(List.of(
                Certification.builder()
                        .name("Certified Software Development Professional")
                        .organization("International Association of Software Architects")
                        .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSzSDVQP9lPdPv9spM7ngSmJyESY7vLrKtkcA&s")
                        .counselor(counselor1)
                        .build(),
                Certification.builder()
                        .name("Certified Career Coach")
                        .organization("International Coach Federation")
                        .imageUrl("https://i0.wp.com/careercoachcertification.in/wp-content/uploads/2023/09/CCT_Certificate_Sample-min.jpg?fit=1000%2C707&ssl=1")
                        .counselor(counselor1)
                        .build()
        ));

        // Tạo Female counselor - Trần Minh Tuấn
        Counselor counselor2 = createAcademicCounselor(0, Gender.FEMALE, "Trần Minh Tuấn", seMajor, counselingSlots, "tuanmta");
        counselor2 = counselorRepository.findById(counselor2.getId()).get();

        counselor2.setAchievements("Developed a coding bootcamp that has successfully trained 150+ students in full-stack development.\n"
                + "Speaker at various tech conferences on career growth and skills in IT.");
        counselor2.setWorkHistory("Lead IT Career Advisor at Hanoi University of Science and Technology (2021–Present):\n"
                + "- Created career resources for software engineering students.\n"
                + "- Guided students through the process of landing their first jobs in tech companies.\n\n"
                + "Junior Software Developer at TechWorld Solutions (2018–2020):\n"
                + "- Developed mobile applications using Java and Kotlin.\n"
                + "- Collaborated with the development team to improve app performance.");
        counselor2.setSpecializedSkills("Full-stack development\n"
                + "Career development for software engineers\n"
                + "Coding bootcamp organization\n"
                + "Technical skill assessment.");
        counselor2.setOtherSkills("Project management\n"
                + "Event coordination\n"
                + "Fluent in Vietnamese, English, and conversational Japanese.");
        counselor2.setQualifications(List.of(
                Qualification.builder()
                        .degree("Bachelor")
                        .fieldOfStudy("Software Engineering")
                        .institution("Hanoi University of Science and Technology")
                        .yearOfGraduation(2018)
                        .imageUrl("https://usth.edu.vn/en/wp-content/uploads/sites/2/2023/09/5f299ed7de040b5a5215.jpg")
                        .counselor(counselor2)
                        .build(),
                Qualification.builder()
                        .degree("Master")
                        .fieldOfStudy("Information Technology")
                        .institution("University of Tokyo")
                        .yearOfGraduation(2021)
                        .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQlW49dCflH-r7lwPcWD6rnbEENfEi9F1rOfA&s")
                        .counselor(counselor2)
                        .build()
        ));
        counselor2.setCertifications(List.of(
                Certification.builder()
                        .name("Certified Full-Stack Developer")
                        .organization("The Coding Academy")
                        .imageUrl("https://campus.w3schools.com/cdn/shop/files/9_8e3e7b66-2ae4-4b44-987f-d991977af19a_700x700.png?v=1720979589")
                        .counselor(counselor2)
                        .build(),
                Certification.builder()
                        .name("Certified Java Developer")
                        .organization("Oracle Corporation")
                        .imageUrl("https://i0.wp.com/umbrellait.com/wp-content/uploads/2021/04/group-1-min.jpg?ssl=1")
                        .counselor(counselor2)
                        .build(),
                Certification.builder()
                        .name("Certified Agile Practitioner")
                        .organization("Scrum Alliance")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build()
        ));

        counselorRepository.save(counselor1);
        counselorRepository.save(counselor2);
    }

    @Transactional
    private void createAIDepartmentCounselor(List<CounselingSlot> counselingSlots) {
        Major aiMajor = majorRepository
                .findByName("Artificial Intelligence")
                .orElseThrow(() -> new NotFoundException("Not found AI major"));

        // Tạo Female counselor - Nguyễn Thị Hồng Nhung
        Counselor counselor1 = createAcademicCounselor(0, Gender.FEMALE, "Nguyễn Thị Hồng Nhung", aiMajor, counselingSlots, "nhungnt");
        counselor1 = counselorRepository.findById(counselor1.getId()).get();

        counselor1.setAchievements("Published a paper on 'Applications of NLP in Healthcare' in 2023.\n"
                + "Developed a chatbot system used in hospitals to assist patients with basic medical information.");
        counselor1.setWorkHistory("AI Researcher at Google AI (2020–Present):\n"
                + "- Conducted research on Natural Language Processing (NLP) applications in healthcare.\n"
                + "- Led a team to create AI-driven solutions for virtual assistants.\n\n"
                + "AI Engineer at FPT Software (2016–2020):\n"
                + "- Worked on implementing Machine Learning models for customer service chatbots.\n"
                + "- Designed data pipelines to support real-time analytics.");
        counselor1.setSpecializedSkills("Natural Language Processing (NLP)\n"
                + "Machine Learning algorithms\n"
                + "AI-driven chatbot development\n"
                + "Data pipeline design.");
        counselor1.setOtherSkills("Excellent communication skills\n"
                + "Team collaboration\n"
                + "Fluent in Vietnamese and English.");
        counselor1.setQualifications(List.of(
                Qualification.builder()
                        .degree("Bachelor")
                        .fieldOfStudy("Computer Science")
                        .institution("Vietnam National University")
                        .yearOfGraduation(2015)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build(),
                Qualification.builder()
                        .degree("Ph.D.")
                        .fieldOfStudy("Machine Learning")
                        .institution("Massachusetts Institute of Technology (MIT)")
                        .yearOfGraduation(2023)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build()
        ));
        counselor1.setCertifications(List.of(
                Certification.builder()
                        .name("Certified AI Specialist")
                        .organization("AI Global Institute")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build(),
                Certification.builder()
                        .name("Certified NLP Engineer")
                        .organization("DeepLearning.AI")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build()
        ));

        // Tạo Male counselor - Trần Quốc Bảo
        Counselor counselor2 = createAcademicCounselor(0, Gender.MALE, "Trần Quốc Bảo", aiMajor, counselingSlots, "baotq");
        counselor2 = counselorRepository.findById(counselor2.getId()).get();

        counselor2.setAchievements("Developed an AI-powered medical imaging analysis system.\n"
                + "Speaker at the 2023 International Conference on Computer Vision.");
        counselor2.setWorkHistory("AI Scientist at VinAI Research (2021–Present):\n"
                + "- Led projects on computer vision applications for healthcare.\n"
                + "- Published multiple research papers in top-tier AI conferences.\n\n"
                + "AI Research Assistant at Stanford University (2018–2021):\n"
                + "- Contributed to research in Reinforcement Learning.\n"
                + "- Assisted in developing AI tools for autonomous vehicles.");
        counselor2.setSpecializedSkills("Computer Vision\n"
                + "Reinforcement Learning\n"
                + "Medical imaging with AI\n"
                + "Autonomous systems.");
        counselor2.setOtherSkills("Project management\n"
                + "Technical writing and presentation\n"
                + "Fluent in Vietnamese, English, and conversational Japanese.");
        counselor2.setQualifications(List.of(
                Qualification.builder()
                        .degree("Bachelor")
                        .fieldOfStudy("Artificial Intelligence")
                        .institution("University of Science, HCMC")
                        .yearOfGraduation(2017)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build(),
                Qualification.builder()
                        .degree("Ph.D.")
                        .fieldOfStudy("Computer Vision")
                        .institution("Stanford University")
                        .yearOfGraduation(2021)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build()
        ));
        counselor2.setCertifications(List.of(
                Certification.builder()
                        .name("Certified Computer Vision Expert")
                        .organization("AI Research Society")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build(),
                Certification.builder()
                        .name("Certified AI Project Manager")
                        .organization("PMI Institute")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build()
        ));

        counselorRepository.save(counselor1);
        counselorRepository.save(counselor2);
    }

    @Transactional
    private void createISDepartmentCounselor(List<CounselingSlot> counselingSlots) {
        Major isMajor = majorRepository
                .findByName("Information Security")
                .orElseThrow(() -> new NotFoundException("Not found Information Security major"));

        // Tạo Female counselor - Lê Thị Thanh Hà
        Counselor counselor1 = createAcademicCounselor(0, Gender.FEMALE, "Lê Thị Thanh Hà", isMajor, counselingSlots, "haltt");
        counselor1 = counselorRepository.findById(counselor1.getId()).get();

        counselor1.setAchievements("Led a team to mitigate a massive DDoS attack on a government network in 2021.\n"
                + "Developed a framework for network intrusion detection, adopted by several large enterprises.");
        counselor1.setWorkHistory("Network Security Specialist at Viettel (2017–Present):\n"
                + "- Designed and implemented secure network infrastructures.\n"
                + "- Conducted threat analysis and implemented mitigation strategies.\n\n"
                + "Cyber Threat Analyst at BKAV (2013–2017):\n"
                + "- Monitored and responded to advanced persistent threats (APT).\n"
                + "- Delivered cybersecurity training to technical teams.");
        counselor1.setSpecializedSkills("Network Security\n"
                + "Cyber Threat Hunting\n"
                + "DDoS Mitigation\n"
                + "Firewall Management.");
        counselor1.setOtherSkills("Strong communication skills\n"
                + "Team leadership\n"
                + "Fluent in Vietnamese and English.");
        counselor1.setQualifications(List.of(
                Qualification.builder()
                        .degree("Bachelor")
                        .fieldOfStudy("Information Technology")
                        .institution("Vietnam National University")
                        .yearOfGraduation(2013)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build()
        ));
        counselor1.setCertifications(List.of(
                Certification.builder()
                        .name("Certified Ethical Hacker (CEH)")
                        .organization("EC-Council")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build(),
                Certification.builder()
                        .name("Certified Information Systems Security Professional (CISSP)")
                        .organization("(ISC)²")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build()
        ));

        // Tạo Male counselor - Nguyễn Văn Hoàng
        Counselor counselor2 = createAcademicCounselor(0, Gender.MALE, "Nguyễn Văn Hung", isMajor, counselingSlots, "hungnv");
        counselor2 = counselorRepository.findById(counselor2.getId()).get();

        counselor2.setAchievements("Discovered multiple zero-day vulnerabilities in web applications.\n"
                + "Built a security testing framework now used by several startups.");
        counselor2.setWorkHistory("Application Security Engineer at KMS Technology (2018–Present):\n"
                + "- Performed penetration testing and vulnerability assessments on enterprise applications.\n"
                + "- Automated security testing in CI/CD pipelines.\n\n"
                + "Security Consultant at PwC (2015–2018):\n"
                + "- Assisted clients in developing secure software development lifecycles (SDLC).\n"
                + "- Delivered workshops on secure coding practices.");
        counselor2.setSpecializedSkills("Application Security\n"
                + "Penetration Testing\n"
                + "Vulnerability Assessment\n"
                + "Secure Software Development Lifecycle (SDLC).");
        counselor2.setOtherSkills("Strong problem-solving skills\n"
                + "Public speaking\n"
                + "Fluent in Vietnamese, English, and basic German.");
        counselor2.setQualifications(List.of(
                Qualification.builder()
                        .degree("Bachelor")
                        .fieldOfStudy("Cybersecurity")
                        .institution("HCMC University of Technology")
                        .yearOfGraduation(2015)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build()
        ));
        counselor2.setCertifications(List.of(
                Certification.builder()
                        .name("Offensive Security Certified Professional (OSCP)")
                        .organization("Offensive Security")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build(),
                Certification.builder()
                        .name("Certified Secure Software Lifecycle Professional (CSSLP)")
                        .organization("(ISC)²")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build()
        ));

        counselorRepository.save(counselor1);
        counselorRepository.save(counselor2);
    }

    @Transactional
    private void createDigitalMarketingDepartmentCounselor(List<CounselingSlot> counselingSlots) {
        Major dmMajor = majorRepository
                .findByName("Digital Marketing")
                .orElseThrow(() -> new NotFoundException("Not found Digital Marketing major"));

        // Tạo Female counselor - Nguyễn Thị Mai Linh
        Counselor counselor1 = createAcademicCounselor(0, Gender.FEMALE, "Nguyễn Thị Mai Linh", dmMajor, counselingSlots, "linhnm");
        counselor1 = counselorRepository.findById(counselor1.getId()).get();

        counselor1.setAchievements("Launched successful campaigns for 50+ clients, with an average 30% increase in social media engagement.\n"
                + "Winner of 'Best Content Strategy' award at the Vietnam Marketing Summit 2023.");
        counselor1.setWorkHistory("Social Media Strategist at Tiktok Vietnam (2019–Present):\n"
                + "- Designed content calendars and executed social media campaigns.\n"
                + "- Increased follower count by 200% across major platforms.\n\n"
                + "Content Creator at Shopee (2016–2019):\n"
                + "- Created engaging content for e-commerce audiences.\n"
                + "- Partnered with influencers to enhance brand visibility.");
        counselor1.setSpecializedSkills("Social Media Marketing\n"
                + "Content Creation\n"
                + "Influencer Marketing\n"
                + "Campaign Management.");
        counselor1.setOtherSkills("Creative thinking\n"
                + "Strong collaboration skills\n"
                + "Fluent in Vietnamese and English.");
        counselor1.setQualifications(List.of(
                Qualification.builder()
                        .degree("Bachelor")
                        .fieldOfStudy("Marketing and Communication")
                        .institution("HCMC University of Social Sciences and Humanities")
                        .yearOfGraduation(2015)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build()
        ));
        counselor1.setCertifications(List.of(
                Certification.builder()
                        .name("Facebook Certified Digital Marketing Associate")
                        .organization("Meta")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build(),
                Certification.builder()
                        .name("Content Marketing Specialist (HubSpot)")
                        .organization("HubSpot Academy")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build()
        ));

        // Tạo Male counselor - Trần Quốc Khánh
        Counselor counselor2 = createAcademicCounselor(0, Gender.MALE, "Trần Quốc Khánh", dmMajor, counselingSlots, "khanhtq");
        counselor2 = counselorRepository.findById(counselor2.getId()).get();

        counselor2.setAchievements("Managed SEO for 100+ clients, achieving top rankings for competitive keywords.\n"
                + "Generated $1M in ad revenue through targeted PPC campaigns in 2022.");
        counselor2.setWorkHistory("SEO Specialist at Google Vietnam (2020–Present):\n"
                + "- Improved organic traffic by 50% for 30+ clients.\n"
                + "- Optimized on-page and off-page SEO strategies.\n\n"
                + "PPC Campaign Manager at Lazada (2017–2020):\n"
                + "- Managed a $2M advertising budget effectively.\n"
                + "- Achieved 300% ROI on high-performing campaigns.");
        counselor2.setSpecializedSkills("Search Engine Optimization (SEO)\n"
                + "Pay-Per-Click Advertising (PPC)\n"
                + "Google Analytics\n"
                + "Keyword Research.");
        counselor2.setOtherSkills("Data analysis\n"
                + "Attention to detail\n"
                + "Fluent in Vietnamese and English.");
        counselor2.setQualifications(List.of(
                Qualification.builder()
                        .degree("Bachelor")
                        .fieldOfStudy("Business Administration")
                        .institution("RMIT Vietnam")
                        .yearOfGraduation(2014)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build()
        ));
        counselor2.setCertifications(List.of(
                Certification.builder()
                        .name("Google Ads Search Certification")
                        .organization("Google")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build(),
                Certification.builder()
                        .name("SEO Certified Professional (Yoast)")
                        .organization("Yoast Academy")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build()
        ));

        counselorRepository.save(counselor1);
        counselorRepository.save(counselor2);
    }

    @Transactional
    private void createInternationalBusinessDepartmentCounselor(List<CounselingSlot> counselingSlots) {
        Major ibMajor = majorRepository
                .findByName("International Business")
                .orElseThrow(() -> new NotFoundException("Not found International Business major"));

        // Tạo Female counselor - Lê Thu Trang
        Counselor counselor1 = createAcademicCounselor(0, Gender.FEMALE, "Lê Thu Trang", ibMajor, counselingSlots, "tranglt");
        counselor1 = counselorRepository.findById(counselor1.getId()).get();

        counselor1.setAchievements("Successfully led market entry projects in 10+ countries, achieving 20% higher-than-expected market share in 3 years.\n"
                + "Published 5 research papers on cross-cultural business negotiations.");
        counselor1.setWorkHistory("International Market Strategist at Deloitte (2018–Present):\n"
                + "- Advised Fortune 500 companies on global market entry.\n"
                + "- Conducted workshops on cross-cultural business strategies.\n\n"
                + "Consultant at McKinsey & Company (2015–2018):\n"
                + "- Delivered strategies for market expansion in Asia-Pacific.\n"
                + "- Worked with C-level executives to improve negotiation outcomes.");
        counselor1.setSpecializedSkills("Global Market Entry Strategies\n"
                + "Cross-Cultural Negotiations\n"
                + "Strategic Planning\n"
                + "International Business Development.");
        counselor1.setOtherSkills("Strong interpersonal communication\n"
                + "Public speaking\n"
                + "Fluent in English, Vietnamese, and Mandarin.");
        counselor1.setQualifications(List.of(
                Qualification.builder()
                        .degree("Master")
                        .fieldOfStudy("International Business Management")
                        .institution("London Business School")
                        .yearOfGraduation(2014)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build()
        ));
        counselor1.setCertifications(List.of(
                Certification.builder()
                        .name("Certified International Trade Professional (CITP)")
                        .organization("Forum for International Trade Training (FITT)")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build(),
                Certification.builder()
                        .name("Cross-Cultural Business Communication Specialist")
                        .organization("Cultural Intelligence Center")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build()
        ));

        // Tạo Male counselor - Phạm Quang Huy
        Counselor counselor2 = createAcademicCounselor(0, Gender.MALE, "Phạm Quang Huy", ibMajor, counselingSlots, "huypq");
        counselor2 = counselorRepository.findById(counselor2.getId()).get();

        counselor2.setAchievements("Streamlined global supply chains for 20+ companies, reducing costs by 15% on average.\n"
                + "Authored a guide on international trade compliance for ASEAN exporters.");
        counselor2.setWorkHistory("Supply Chain Manager at Samsung Electronics (2017–Present):\n"
                + "- Led global supply chain optimization projects.\n"
                + "- Collaborated with customs authorities to ensure compliance.\n\n"
                + "Trade Compliance Officer at Maersk Line (2013–2017):\n"
                + "- Managed import/export documentation for high-value shipments.\n"
                + "- Trained teams on international trade regulations.");
        counselor2.setSpecializedSkills("International Trade Compliance\n"
                + "Supply Chain Management\n"
                + "Logistics Optimization\n"
                + "Customs Clearance.");
        counselor2.setOtherSkills("Analytical thinking\n"
                + "Problem-solving\n"
                + "Fluent in Vietnamese and English.");
        counselor2.setQualifications(List.of(
                Qualification.builder()
                        .degree("Master")
                        .fieldOfStudy("Global Supply Chain Management")
                        .institution("University of Melbourne")
                        .yearOfGraduation(2013)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build()
        ));
        counselor2.setCertifications(List.of(
                Certification.builder()
                        .name("Customs Compliance Certification (CCP)")
                        .organization("International Compliance Professionals Association (ICPA)")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build(),
                Certification.builder()
                        .name("Certified Supply Chain Professional (CSCP)")
                        .organization("APICS")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build()
        ));

        counselorRepository.save(counselor1);
        counselorRepository.save(counselor2);
    }

    @Transactional
    private void createFinanceDepartmentCounselor(List<CounselingSlot> counselingSlots) {
        Major financeMajor = majorRepository
                .findByName("Finance")
                .orElseThrow(() -> new NotFoundException("Not found Finance major"));

        // Tạo Female counselor - Nguyễn Thị Mai
        Counselor counselor1 = createAcademicCounselor(0, Gender.FEMALE, "Nguyễn Thị Mai", financeMajor, counselingSlots, "mainn");
        counselor1 = counselorRepository.findById(counselor1.getId()).get();

        counselor1.setAchievements("Managed a portfolio worth $100M with an annualized return of 12% over 5 years.\n"
                + "Authored 3 white papers on quantitative portfolio optimization strategies.");
        counselor1.setWorkHistory("Portfolio Manager at BlackRock (2015–Present):\n"
                + "- Managed diversified investment portfolios for high-net-worth clients.\n"
                + "- Developed data-driven investment strategies to optimize returns.\n\n"
                + "Equity Analyst at JP Morgan (2012–2015):\n"
                + "- Conducted market research and analyzed equity performance trends.\n"
                + "- Prepared investment recommendations for institutional clients.");
        counselor1.setSpecializedSkills("Portfolio Optimization\n"
                + "Equity Research\n"
                + "Investment Strategy Development\n"
                + "Financial Market Analysis.");
        counselor1.setOtherSkills("Data analysis\n"
                + "Critical thinking\n"
                + "Fluent in Vietnamese, English, and French.");
        counselor1.setQualifications(List.of(
                Qualification.builder()
                        .degree("Master")
                        .fieldOfStudy("Finance and Investment")
                        .institution("University of Cambridge")
                        .yearOfGraduation(2011)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build()
        ));
        counselor1.setCertifications(List.of(
                Certification.builder()
                        .name("Chartered Financial Analyst (CFA)")
                        .organization("CFA Institute")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build(),
                Certification.builder()
                        .name("Certified Investment Management Analyst (CIMA)")
                        .organization("Investment Management Consultants Association (IMCA)")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build()
        ));

        // Tạo Male counselor - Trần Văn Hùng
        Counselor counselor2 = createAcademicCounselor(0, Gender.MALE, "Trần Văn Hùng", financeMajor, counselingSlots, "hungtv");
        counselor2 = counselorRepository.findById(counselor2.getId()).get();

        counselor2.setAchievements("Reduced financial risk exposure by 15% through robust risk management frameworks.\n"
                + "Advised Fortune 500 companies on optimizing capital structure.");
        counselor2.setWorkHistory("Risk Manager at HSBC (2016–Present):\n"
                + "- Designed comprehensive risk mitigation strategies for corporate clients.\n"
                + "- Conducted financial stress tests to evaluate risk exposure.\n\n"
                + "Corporate Finance Analyst at KPMG (2013–2016):\n"
                + "- Provided strategic financial advice for mergers and acquisitions.\n"
                + "- Analyzed financial statements to improve cost efficiency.");
        counselor2.setSpecializedSkills("Corporate Finance Analysis\n"
                + "Risk Management\n"
                + "Capital Budgeting\n"
                + "Mergers and Acquisitions.");
        counselor2.setOtherSkills("Problem-solving\n"
                + "Strong interpersonal skills\n"
                + "Fluent in Vietnamese and English.");
        counselor2.setQualifications(List.of(
                Qualification.builder()
                        .degree("Master")
                        .fieldOfStudy("Corporate Finance")
                        .institution("London School of Economics")
                        .yearOfGraduation(2012)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build()
        ));
        counselor2.setCertifications(List.of(
                Certification.builder()
                        .name("Financial Risk Manager (FRM)")
                        .organization("Global Association of Risk Professionals (GARP)")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build(),
                Certification.builder()
                        .name("Certified Management Accountant (CMA)")
                        .organization("Institute of Management Accountants (IMA)")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build()
        ));

        counselorRepository.save(counselor1);
        counselorRepository.save(counselor2);
    }

    @Transactional
    private void createMultimediaCommunicationCounselor(List<CounselingSlot> counselingSlots) {
        Major multimediaMajor = majorRepository
                .findByName("Multimedia Communication")
                .orElseThrow(() -> new NotFoundException("Not found Multimedia Communication major"));

        // Tạo Female counselor - Lê Thị Phương Anh
        Counselor counselor1 = createAcademicCounselor(0, Gender.FEMALE, "Lê Thị Phương Anh", multimediaMajor, counselingSlots, "phuonganhlt");
        counselor1 = counselorRepository.findById(counselor1.getId()).get();

        counselor1.setAchievements("Successfully managed over 50 digital marketing campaigns with a 20% increase in engagement rates.\n"
                + "Published 10 articles on storytelling techniques in top-tier media journals.");
        counselor1.setWorkHistory("Digital Marketing Manager at Ogilvy Vietnam (2017–Present):\n"
                + "- Developed creative content strategies for multinational brands.\n"
                + "- Oversaw a team of 10 content creators for high-impact campaigns.\n\n"
                + "Content Strategist at Dentsu Aegis Network (2014–2017):\n"
                + "- Conducted market research to enhance digital storytelling.\n"
                + "- Pioneered innovative approaches in social media engagement.");
        counselor1.setSpecializedSkills("Digital Storytelling\n"
                + "Content Strategy Development\n"
                + "Brand Marketing\n"
                + "Social Media Optimization.");
        counselor1.setOtherSkills("Public speaking\n"
                + "Creative writing\n"
                + "Fluent in Vietnamese, English, and Spanish.");
        counselor1.setQualifications(List.of(
                Qualification.builder()
                        .degree("Master")
                        .fieldOfStudy("Digital Communication")
                        .institution("University of Southern California")
                        .yearOfGraduation(2013)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build()
        ));
        counselor1.setCertifications(List.of(
                Certification.builder()
                        .name("Certified Content Marketing Specialist (CCMS)")
                        .organization("Digital Marketing Institute")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build(),
                Certification.builder()
                        .name("Storytelling in the Digital Age")
                        .organization("New York University")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build()
        ));

        // Tạo Male counselor - Nguyễn Văn Hoàng
        Counselor counselor2 = createAcademicCounselor(0, Gender.MALE, "Nguyễn Văn Hoàng", multimediaMajor, counselingSlots, "hoangnv");
        counselor2 = counselorRepository.findById(counselor2.getId()).get();

        counselor2.setAchievements("Directed over 20 successful TV commercials for top-tier brands.\n"
                + "Recipient of the 'Best VFX in Commercials' award in 2021.");
        counselor2.setWorkHistory("VFX Supervisor at MPC Film (2018–Present):\n"
                + "- Led post-production teams for high-budget film projects.\n"
                + "- Developed cutting-edge visual effects workflows.\n\n"
                + "Media Producer at Saatchi & Saatchi (2014–2018):\n"
                + "- Managed end-to-end production for multimedia advertisements.\n"
                + "- Collaborated with clients to ensure brand alignment.");
        counselor2.setSpecializedSkills("Media Production\n"
                + "Visual Effects (VFX)\n"
                + "Post-Production Management\n"
                + "Cinematography.");
        counselor2.setOtherSkills("Problem-solving\n"
                + "Team leadership\n"
                + "Fluent in Vietnamese and English.");
        counselor2.setQualifications(List.of(
                Qualification.builder()
                        .degree("Bachelor")
                        .fieldOfStudy("Film and Television Production")
                        .institution("New York Film Academy")
                        .yearOfGraduation(2012)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build()
        ));
        counselor2.setCertifications(List.of(
                Certification.builder()
                        .name("Adobe Certified Professional in Video Design")
                        .organization("Adobe")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build(),
                Certification.builder()
                        .name("Advanced VFX Techniques")
                        .organization("Gnomon School of Visual Effects")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build()
        ));

        counselorRepository.save(counselor1);
        counselorRepository.save(counselor2);
    }

    @Transactional
    private void createEnglishLanguageCounselor(List<CounselingSlot> counselingSlots) {
        Major englishMajor = majorRepository
                .findByName("English Language")
                .orElseThrow(() -> new NotFoundException("Not found English Language major"));

        // Tạo Female counselor - Nguyễn Thị Minh Châu
        Counselor counselor1 = createAcademicCounselor(0, Gender.FEMALE, "Nguyễn Thị Minh Châu", englishMajor, counselingSlots, "chauntm");
        counselor1 = counselorRepository.findById(counselor1.getId()).get();

        counselor1.setAchievements("Taught over 1000 hours of ESL courses to international students.\n"
                + "Helped students achieve high scores on IELTS and TOEFL exams.");
        counselor1.setWorkHistory("ESL Instructor at British Council (2015–Present):\n"
                + "- Conducted courses focused on academic English and professional communication.\n"
                + "- Trained students for IELTS and TOEFL exams.\n\n"
                + "TESOL Educator at University of Cambridge (2012–2015):\n"
                + "- Developed language training programs for non-native speakers.\n"
                + "- Led workshops on academic writing skills.");
        counselor1.setSpecializedSkills("ESL Teaching\n"
                + "IELTS/TOEFL Preparation\n"
                + "Academic Writing\n"
                + "Pronunciation and Speaking.");
        counselor1.setOtherSkills("Cross-cultural communication\n"
                + "Fluent in Vietnamese and English.");
        counselor1.setQualifications(List.of(
                Qualification.builder()
                        .degree("Master")
                        .fieldOfStudy("Applied Linguistics")
                        .institution("University of Cambridge")
                        .yearOfGraduation(2012)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build()
        ));
        counselor1.setCertifications(List.of(
                Certification.builder()
                        .name("CELTA - Certificate in Teaching English to Speakers of Other Languages")
                        .organization("University of Cambridge")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build(),
                Certification.builder()
                        .name("TESOL - Teaching English to Speakers of Other Languages")
                        .organization("TESOL International Association")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor1)
                        .build()
        ));

        // Tạo Male counselor - Trần Minh Tùng
        Counselor counselor2 = createAcademicCounselor(0, Gender.MALE, "Trần Minh Tùng", englishMajor, counselingSlots, "tungtm");
        counselor2 = counselorRepository.findById(counselor2.getId()).get();

        counselor2.setAchievements("Published 5 academic papers on English Literature.\n"
                + "Guest speaker at international linguistics conferences.");
        counselor2.setWorkHistory("Professor of English Literature at Hanoi University (2010–Present):\n"
                + "- Teaches courses on English literature and literary analysis.\n"
                + "- Guides students on research projects related to literary theory.\n\n"
                + "Linguistics Researcher at Vietnam National University (2005–2010):\n"
                + "- Conducted studies on English phonetics and semantics.\n"
                + "- Developed academic material for linguistics courses.");
        counselor2.setSpecializedSkills("English Literature\n"
                + "Linguistics\n"
                + "Literary Theory\n"
                + "Phonetics and Semantics.");
        counselor2.setOtherSkills("Research and publication\n"
                + "Mentoring students\n"
                + "Fluent in Vietnamese and English.");
        counselor2.setQualifications(List.of(
                Qualification.builder()
                        .degree("PhD")
                        .fieldOfStudy("English Literature")
                        .institution("University of London")
                        .yearOfGraduation(2005)
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build()
        ));
        counselor2.setCertifications(List.of(
                Certification.builder()
                        .name("Master's in English Literature")
                        .organization("University of London")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build(),
                Certification.builder()
                        .name("PhD in Linguistics")
                        .organization("University of London")
                        .imageUrl("https://www.agile42.com/en/wp-content/uploads/sites/2/2024/07/CSP-SM_grey.png")
                        .counselor(counselor2)
                        .build()
        ));

        counselorRepository.save(counselor1);
        counselorRepository.save(counselor2);
    }

    @Transactional
    private void seedCounselorAccounts() {
        List<CounselingSlot> counselingSlots = counselingSlotRepository.findAll();

        // Danh sách chuyên môn
        List<String> expertiseNames = List.of("School Psychologist", "School Health Advisor", "Career Counseling");

        // Tạo chuyên môn nếu chưa tồn tại
        List<Expertise> expertiseList = expertiseNames.stream()
                .map(name -> expertiseRepository.findByName(name)
                        .orElseGet(() -> expertiseRepository.save(Expertise.builder().name(name).build())))
                .toList();

        // non-academic
        createSchoolPsychologists(expertiseList, counselingSlots);
        createSchoolHealthAdvisors(expertiseList, counselingSlots);
        createCareerCounselors(expertiseList, counselingSlots);

        // academic
        createITDepartmentCounselor(counselingSlots);
        createAIDepartmentCounselor(counselingSlots);
        createISDepartmentCounselor(counselingSlots);
        createDigitalMarketingDepartmentCounselor(counselingSlots);
        createInternationalBusinessDepartmentCounselor(counselingSlots);
        createFinanceDepartmentCounselor(counselingSlots);
        createMultimediaCommunicationCounselor(counselingSlots);
        createEnglishLanguageCounselor(counselingSlots);
    }

    private Counselor createAcademicCounselor(int index, Gender gender, String fullName, Major major, List<CounselingSlot> counselingSlots, String counselorEmail) {
        int[] startAndEnd = RandomUtil.getRandomStartEnd(0, counselingSlots.size(), 4);
        List<CounselingSlot> counselorSlots = getCounselorSlot(startAndEnd, counselingSlots);

//        String counselorEmail = "ac" + ((gender == Gender.FEMALE) ? "f" : "m") + (index + 1);

        logger.info("Checking if academic counselor account with email '{}' exists.", counselorEmail);

        if (accountRepository.findAccountByEmail(counselorEmail).isEmpty()) {
            logger.info("Academic counselor account does not exist. Creating new account for specialization '{}'.", major.getName());

            counselorEmail += "@gmail.com";

            Account counselor = Account.builder()
                    .email(counselorEmail)
                    .role(Role.ACADEMIC_COUNSELOR)
                    .status(Status.ACTIVE)
                    .password(passwordEncoder.encode("112233"))
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
//                    .rating(BigDecimal.valueOf(0))
                    .gender(gender) // Adjust as needed
//                    .specialization(specialization)
                    .major(major)
                    .department(major.getDepartment())
                    .status(CounselorStatus.AVAILABLE)
//                    .counselingSlots(counselorSlots)
                    .academicDegree("Thạc sĩ") // Adjust degree as needed
                    .build();

            AvailableDateRange availableDateRange = createAvailableDateRangeFromTodayToTwoMonths(counselorProfile);
            counselorProfile.setAvailableDateRange(availableDateRange);
            List<SlotOfCounselor> slotOfCounselors = getSlotsForCounselorByDay(counselingSlots, counselorProfile);

            profileRepository.save(counselorProfile);
            slotOfCounselorRepository.saveAll(slotOfCounselors);

            logger.info("Academic counselor account created with email '{}'.", counselorEmail);
            return counselorProfile;
        } else {
            logger.warn("Academic counselor account with email '{}' already exists.", counselorEmail);
        }
        return null;
    }

    private Counselor createNonAcademicCounselor(int index, Gender gender, String fullName, Expertise expertise, List<CounselingSlot> counselingSlots, String counselorEmail) {
        int[] startAndEnd = RandomUtil.getRandomStartEnd(0, counselingSlots.size(), 4);
        List<CounselingSlot> counselorSlots = getCounselorSlot(startAndEnd, counselingSlots);

//        String counselorEmail = "nac" + ((gender == Gender.FEMALE) ? "f" : "m") + (index + 1);

        logger.info("Checking if non-academic counselor account with email '{}' exists.", counselorEmail);

        if (accountRepository.findAccountByEmail(counselorEmail).isEmpty()) {
            logger.info("Non-academic counselor account does not exist. Creating new account for expertise '{}'.", expertise.getName());

            counselorEmail += "@gmail.com";

            Account counselor = Account.builder()
                    .email(counselorEmail)
                    .role(Role.NON_ACADEMIC_COUNSELOR)
                    .status(Status.ACTIVE)
                    .password(passwordEncoder.encode("112233"))
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
//                    .rating(BigDecimal.valueOf(0))
                    .gender(gender) // Adjust as needed
                    .expertise(expertise)
                    .industryExperience(5) // Set an example for industry experience
                    .status(CounselorStatus.AVAILABLE)
//                    .counselingSlots(counselorSlots)
                    .build();

            AvailableDateRange availableDateRange = createAvailableDateRangeFromTodayToTwoMonths(counselorProfile);
            counselorProfile.setAvailableDateRange(availableDateRange);
            List<SlotOfCounselor> slotOfCounselors = getSlotsForCounselorByDay(counselingSlots, counselorProfile);

            profileRepository.save(counselorProfile);
            slotOfCounselorRepository.saveAll(slotOfCounselors);
            logger.info("Non-academic counselor account created with email '{}'.", counselorEmail);
            return counselorProfile;
        } else {
            logger.warn("Non-academic counselor account with email '{}' already exists.", counselorEmail);
        }
        return null;
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

    private List<SlotOfCounselor> getSlotsForCounselorByDay(List<CounselingSlot> counselingSlots, Counselor counselor) {

        List<DayOfWeek> dayOfWeeks = new ArrayList<>();
        dayOfWeeks.add(DayOfWeek.MONDAY);
        dayOfWeeks.add(DayOfWeek.TUESDAY);
        dayOfWeeks.add(DayOfWeek.WEDNESDAY);
        dayOfWeeks.add(DayOfWeek.THURSDAY);
        dayOfWeeks.add(DayOfWeek.FRIDAY);

        List<SlotOfCounselor> dailySlots = new ArrayList<>();

        for(DayOfWeek dayOfWeek : dayOfWeeks) {
            int[] startAndEnd = RandomUtil.getRandomStartEnd(0, counselingSlots.size(), 4);
            List<CounselingSlot> counselorSlots = getCounselorSlot(startAndEnd, counselingSlots);
            for(CounselingSlot slot: counselorSlots) {
                dailySlots.add(SlotOfCounselor.builder()
                                .dayOfWeek(dayOfWeek)
                                .counselingSlot(slot)
                                .counselor(counselor)
                        .build());
            }
        }
        return dailySlots;
    }

//    private void createOnlineCounselingAppointment(CounselingAppointmentRequest appointmentRequest) {
//        OnlineAppointment appointment = OnlineAppointment.builder()
//                .startDateTime(LocalDateTime.of(appointmentRequest.getRequireDate(), appointmentRequest.getStartTime()))
//                .endDateTime(LocalDateTime.of(appointmentRequest.getRequireDate(), appointmentRequest.getEndTime()))
//                .status(CounselingAppointmentStatus.ATTEND)
//                .appointmentRequest(appointmentRequest)
//                .meetUrl("hehehehe")
//                .build();
//
//        counselingAppointmentRepository.save(appointment);
//    }
//
//    private void createOfflineCounselingAppointment(CounselingAppointmentRequest appointmentRequest) {
//        OfflineAppointment appointment = OfflineAppointment.builder()
//                .startDateTime(LocalDateTime.of(appointmentRequest.getRequireDate(), appointmentRequest.getStartTime()))
//                .endDateTime(LocalDateTime.of(appointmentRequest.getRequireDate(), appointmentRequest.getEndTime()))
//                .status(CounselingAppointmentStatus.WAITING)
//                .appointmentRequest(appointmentRequest)
//                .address("hahaha")
//                .build();
//
//        counselingAppointmentRepository.save(appointment);
//    }

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
// Seed Categories

    private final ContributionQuestionCardService questionCardService;
    private final ContributedQuestionCardCategoryRepository categoryRepository;

    private void seedCategories() {
        // Tạo các category nếu chưa có
        if (categoryRepository.count() == 0) {
            // Tạo category cho Software
            ContributedQuestionCardCategory softwareCategory = new ContributedQuestionCardCategory();
            softwareCategory.setName("Software");
            softwareCategory.setType(ContributedQuestionCardCategory.Type.ACADEMIC);
            categoryRepository.save(softwareCategory);

            // Tạo category cho Artificial Intelligence
            ContributedQuestionCardCategory aiCategory = new ContributedQuestionCardCategory();
            aiCategory.setName("Artificial Intelligence");
            aiCategory.setType(ContributedQuestionCardCategory.Type.ACADEMIC);
            categoryRepository.save(aiCategory);

            // Tạo category cho Business
            ContributedQuestionCardCategory businessCategory = new ContributedQuestionCardCategory();
            businessCategory.setName("Business");
            businessCategory.setType(ContributedQuestionCardCategory.Type.ACADEMIC);
            categoryRepository.save(businessCategory);

            // Tạo category cho Stress Management
            ContributedQuestionCardCategory stressCategory = new ContributedQuestionCardCategory();
            stressCategory.setName("Stress Management");
            stressCategory.setType(ContributedQuestionCardCategory.Type.NON_ACADEMIC);
            categoryRepository.save(stressCategory);

            // Tạo category cho Peer Pressure
            ContributedQuestionCardCategory peerPressureCategory = new ContributedQuestionCardCategory();
            peerPressureCategory.setName("Peer Pressure");
            peerPressureCategory.setType(ContributedQuestionCardCategory.Type.NON_ACADEMIC);
            categoryRepository.save(peerPressureCategory);
        }
    }

    private void seedQuestionsAndAttachWithCategory() {
        // Lấy danh sách categories từ DB
        List<ContributedQuestionCardCategory> categories = categoryRepository.findAll();

        if (!categories.isEmpty()) {
            // Lấy các category
            ContributedQuestionCardCategory softwareCategory = categories.stream()
                    .filter(c -> c.getName().equals("Software"))
                    .findFirst().orElseThrow();
            ContributedQuestionCardCategory aiCategory = categories.stream()
                    .filter(c -> c.getName().equals("Artificial Intelligence"))
                    .findFirst().orElseThrow();
            ContributedQuestionCardCategory businessCategory = categories.stream()
                    .filter(c -> c.getName().equals("Business"))
                    .findFirst().orElseThrow();
            ContributedQuestionCardCategory stressCategory = categories.stream()
                    .filter(c -> c.getName().equals("Stress Management"))
                    .findFirst().orElseThrow();
            ContributedQuestionCardCategory peerPressureCategory = categories.stream()
                    .filter(c -> c.getName().equals("Peer Pressure"))
                    .findFirst().orElseThrow();

            // Seed Questions cho từng category
            seedQuestionsForCategory(softwareCategory, Arrays.asList(
                    "What is the difference between supervised and unsupervised learning?",
                    "How does a neural network learn patterns in data?",
                    "What is the role of APIs in software integration?",
                    "How do databases ensure data consistency and integrity?",
                    "Explain the concept of cloud computing and its impact on business.",
                    "Describe the process of DevOps and its importance.",
                    "What are effective ways to master object-oriented programming?",
                    "Which practices ensure clean and maintainable code?",
                    "How can code debugging be done more effectively?",
                    "What strategies are best for excelling in software engineering exams?",
                    "How should group projects in software development be organized?"
            ), Arrays.asList(
                    "Supervised vs Unsupervised Learning",
                    "Neural Network Basics",
                    "APIs in Software Integration",
                    "Database Integrity",
                    "Cloud Computing in Business",
                    "DevOps Essentials",
                    "Focus on grasping the four key principles of OOP: encapsulation, inheritance, abstraction, and polymorphism.",
                    "Choose descriptive variable names, adhere to SOLID principles, and create thorough unit tests.",
                    "Follow a step-by-step process: pinpoint the problem, analyze logs, and utilize a debugger.",
                    "Study previous exam papers, solidify core concepts, and practice solving coding exercises.",
                    "Allocate tasks clearly, establish specific deadlines, and leverage version control systems for tracking."
            ));

            seedQuestionsForCategory(aiCategory, Arrays.asList(
                    "What are the key features of a neural network?",
                    "Explain the concept of overfitting in machine learning.",
                    "What is the difference between artificial intelligence and machine learning?",
                    "How does deep learning differ from traditional machine learning?",
                    "What are the challenges of implementing artificial intelligence in real-world applications?",
                    "How does artificial intelligence contribute to automation in industries?"
            ), Arrays.asList(
                    "Neural Network Features",
                    "Overfitting in AI",
                    "AI vs Machine Learning",
                    "Deep Learning vs Traditional ML",
                    "Challenges in AI Implementation",
                    "AI in Automation"
            ));

            seedQuestionsForCategory(businessCategory, Arrays.asList(
                    "What is the significance of cloud computing in modern business?",
                    "How does digital marketing impact business growth?",
                    "What are the key elements of a business model canvas?",
                    "What are the common challenges in data migration for businesses?",
                    "Explain the importance of financial forecasting in business planning.",
                    "What are the advantages of CRM systems in business?"
            ), Arrays.asList(
                    "Cloud Computing in Business",
                    "Impact of Digital Marketing",
                    "Business Model Canvas",
                    "Data Migration Challenges",
                    "Financial Forecasting Basics",
                    "CRM Systems in Business"
            ));

            seedQuestionsForCategory(stressCategory, Arrays.asList(
                    "What strategies can be used to manage stress among students?",
                    "How does peer pressure affect mental health in schools?",
                    "What are the signs of anxiety in students and how can they be addressed?",
                    "Describe the importance of physical activity for mental health in students.",
                    "How can schools promote healthy eating habits among students?"
            ), Arrays.asList(
                    "Managing Student Stress",
                    "Peer Pressure and Mental Health",
                    "Addressing Anxiety in Students",
                    "Physical Activity for Mental Health",
                    "Promoting Healthy Eating"
            ));

            seedQuestionsForCategory(peerPressureCategory, Arrays.asList(
                    "How does peer pressure affect mental health in schools?",
                    "What are the signs of anxiety in students and how can they be addressed?",
                    "How can students cope with peer pressure effectively?",
                    "What role do teachers play in helping students deal with peer pressure?",
                    "Describe the impact of peer pressure on academic performance."
            ), Arrays.asList(
                    "Peer Pressure and Mental Health",
                    "Addressing Anxiety in Students",
                    "Coping with Peer Pressure",
                    "Teachers' Role in Peer Pressure",
                    "Peer Pressure and Academic Performance"
            ));
        }
    }

    private void seedQuestionsForCategory(ContributedQuestionCardCategory category, List<String> questions, List<String> titles) {
        Counselor counselor = counselorRepository.findAll().get(0); // Chọn counselor

        // Tạo câu hỏi và gán vào category
        for (int i = 0; i < questions.size(); i++) {
            String questionText = questions.get(i);
            String titleText = titles.get(i);
            String answerText = "Sample answer for: " + questionText;

            // Tạo câu hỏi cho category này
            questionCardService.createContributionQuestionCard(
                    questionText,
                    answerText,
                    category.getId(),
                    counselor.getId(),
                    titleText
            );
        }
    }

//    private final EntityManager entityManager;

//    @Transactional
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

    private final StudentSynchronizeService studentSynchronizeService;

    @Transactional
    private void seedStudentStudy() {
        List<Student> students = studentRepository.findAll();
        List<StudentStudy> studentStudies = new ArrayList<>();

        for(Student student : students) {
//            String url = fapServerUrl + "/api/students/" + "study/all-semester/" + student.getStudentCode();
//            AttendanceDTO[] attendanceDTOS = restTemplate.getForObject(url, AttendanceDTO[].class);
//            if(attendanceDTOS != null) {
//                for(AttendanceDTO dto : attendanceDTOS) {
//                    Semester semester = semesterRepository.findByName(dto.getSemesterName()).orElseThrow(() -> new NotFoundException("Not Found Semester For Syn StudentStudy"));
//                    StudentStudy studentStudy = StudentStudyMapper.toStudentStudy(dto, student, semester);
//                    studentStudies.add(studentStudy);
//                }
//
//                if(!studentStudies.isEmpty()) {
//                    batchSaveStudentStudy(studentStudies);
//                }
//            }
            studentSynchronizeService.syncOneStudent(student.getStudentCode());
        }

//        ResponseEntity<StudentFapResponseDTO[]> response = restTemplate.getForEntity(fapServerUrl + "/api/students", StudentFapResponseDTO[].class);
//
//        if (response.getBody() != null) {
//            List<StudentFapResponseDTO> studentDTOs = List.of(response.getBody());
//
//            for (StudentFapResponseDTO studentFapResponseDTO : studentDTOs) {
//                studentSynchronizeService.syncOneStudent(studentFapResponseDTO.getStudentCode());
//            }
//        }
    }

    private final AcademicCounselorRepository academicCounselorRepository;
    private final NonAcademicCounselorRepository nonAcademicCounselorRepository;
    private final QuestionCardDocumentRepository questionCardDocumentRepository;
    private final OpenAIService openAIService;

    private void seedAllQuestionCards() {
        seedQuestionCardForPsychologyCounselor();
        seedQuestionCardForHealthCounselor();
        seedQuestionCardForCareerCounselor();
        seedQuestionCardForSoftwareEngineeringCounselor();
        seedQuestionCardForArtificialIntelligenceCounselor();
        seedQuestionCardForInternationalBusinessCounselor();
        seedQuestionCardForFinanceCounselor();
    }


    private void createQuestionCards(
            List<Counselor> counselors,
            List<Student> students,
            String[] titles,
            String[] contents,
            String[] answers, // Add answers parameter
            QuestionType questionType,
            QuestionCard.QuestionCarDifficultyLevel difficultyLevel) {

        if (titles.length != contents.length || titles.length != answers.length) {
            throw new IllegalArgumentException("Titles, contents, and answers arrays must have the same length.");
        }

        for (int i = 0; i < titles.length; i++) {
            // Select a random counselor from the list
            Counselor counselor = counselors.get((int) (Math.random() * counselors.size()));

            QuestionCard questionCard = QuestionCard.builder()
                    .title(titles[i])
                    .content(contents[i])
                    .answer(answers[i]) // Assign the answer to the question card
                    .questionType(questionType)
                    .difficultyLevel(difficultyLevel)
                    .status(QuestionCardStatus.VERIFIED)
                    .isClosed(true)
                    .publicStatus(QuestionCard.PublicStatus.VISIBLE)
                    .closedDate(LocalDateTime.now())
                    .counselor(counselor)
                    .student(students.get((int) (Math.random() * students.size())))
                    .build();
            questionCardRepository.save(questionCard);

            // Create random feedback for the QuestionCard
            int randomRating = (int) (1 + Math.random() * 5); // Random integer rating between 1 and 5
            String feedbackComment = "This is a generated feedback comment for the question card.";

            QuestionCardFeedback feedback = QuestionCardFeedback.builder()
                    .rating(BigDecimal.valueOf(randomRating))
                    .comment(feedbackComment)
                    .questionCard(questionCard)
                    .counselor(counselor)
                    .build();
            questionCardFeedbackRepository.save(feedback);

            // Lấy vector embedding cho question và title từ OpenAI API
            List<Double> questionVectorDouble = openAIService.getEmbeddingFromOpenAPI(contents[i]);
            List<Double> titleVectorDouble = openAIService.getEmbeddingFromOpenAPI(titles[i]);

            // Chuyển đổi Double -> Float
            List<Float> questionVector = questionVectorDouble.stream()
                    .map(Double::floatValue)
                    .toList();
            List<Float> titleVector = titleVectorDouble.stream()
                    .map(Double::floatValue)
                    .toList();

            // Tạo QuestionCardDocument cho Elasticsearch và lưu vào Elasticsearch
            QuestionCardDocument questionCardDocument = QuestionCardDocument.builder()
                    .id(String.valueOf(questionCard.getId())) // ID from the QuestionCard
                    .sortingId(questionCard.getId())
                    .title(titles[i])
                    .content(contents[i])
                    .answer(answers[i]) // Answers for the document
                    .questionType(questionType.name()) // Converting enum to string
                    .difficultyLevel(difficultyLevel.name()) // Converting enum to string
                    .closedDate(questionCard.getClosedDate()) // The current date for the created time
                    .counselorId(String.valueOf(counselor.getId())) // Counselor ID
                    .studentId(String.valueOf(students.get((int) (Math.random() * students.size())).getId())) // Randomly select student ID
                    .contentVector(questionVector)
                    .titleVector(titleVector)
                    .build();
            questionCardDocumentRepository.save(questionCardDocument);
        }
    }

    private void seedQuestionCardForPsychologyCounselor() {
        List<Counselor> psychologyCounselor = nonAcademicCounselorRepository.findAll()
                .stream()
                .filter(counselor -> counselor.getExpertise().getName().equals("School Psychologist"))
                .map(counselor -> (Counselor) counselor)
                .toList();
//                .findFirst().orElseThrow(() -> new NotFoundException("Not found Psychology counselor for seeding"));

        List<Student> students = studentRepository.findAll();
        String[] titles = {
                "How to manage exam stress?",
                "How to deal with anxiety before a big test?",
                "Tips for improving focus during study sessions.",
                "Effective ways to build self-confidence.",
                "Dealing with peer pressure in school."
        };
        String[] contents = {
                "I am really stressed out due to exams. Can you provide some tips to manage it effectively?",
                "I feel very anxious before tests. How can I overcome this?",
                "I want to improve my focus during study sessions. What strategies can I use?",
                "I lack self-confidence and struggle to speak up in class. Any advice?",
                "My friends often pressure me into things I don't want to do. How do I handle it?"
        };
        String[] answers = {
                "Try deep breathing exercises, time management, and regular breaks to reduce stress.",
                "Practice relaxation techniques like meditation and focus on positive thoughts.",
                "Use techniques like the Pomodoro method or eliminate distractions during study time.",
                "Start by setting small goals and practicing self-affirmation.",
                "Learn to say no and prioritize your own values and beliefs."
        };

        createQuestionCards(psychologyCounselor, students, titles, contents, answers, QuestionType.NON_ACADEMIC, QuestionCard.QuestionCarDifficultyLevel.Medium);
    }

    private void seedQuestionCardForHealthCounselor() {
        List<Counselor> healthCounselor = nonAcademicCounselorRepository.findAll()
                .stream()
                .filter(counselor -> counselor.getExpertise().getName().equals("School Health Advisor"))
                .map(counselor -> (Counselor) counselor)
                .toList();
//                .findFirst().orElseThrow(() -> new NotFoundException("Not found Health counselor for seeding"));

        List<Student> students = studentRepository.findAll();
        String[] titles = {
                "How to maintain a healthy diet?",
                "Best practices for staying fit as a student.",
                "How to avoid burnout from studying too much?",
                "Ways to improve sleep quality.",
                "How to recover from sickness quickly?"
        };
        String[] contents = {
                "I want to improve my diet as a student. Any suggestions?",
                "What are some exercises I can do to stay fit?",
                "I often feel tired from studying too much. How do I avoid burnout?",
                "I have trouble sleeping well at night. Can you help?",
                "What steps can I take to recover quickly after falling sick?"
        };
        String[] answers = {
                "Eat a balanced diet with a variety of fruits, vegetables, and proteins.",
                "Try exercises like jogging, swimming, or yoga to stay active.",
                "Take regular breaks, exercise, and maintain a work-life balance.",
                "Establish a relaxing bedtime routine and avoid screen time before sleep.",
                "Stay hydrated, eat nutritious food, and rest as much as possible."
        };

        createQuestionCards(healthCounselor, students, titles, contents, answers, QuestionType.NON_ACADEMIC, QuestionCard.QuestionCarDifficultyLevel.Easy);
    }

    private void seedQuestionCardForCareerCounselor() {
        List<Counselor> careerCounselor = nonAcademicCounselorRepository.findAll()
                .stream()
                .filter(counselor -> counselor.getExpertise().getName().equals("Career Counseling"))
                .map(counselor -> (Counselor) counselor)
                .toList();
//                .findFirst().orElseThrow(() -> new NotFoundException("Not found Career counselor for seeding"));

        List<Student> students = studentRepository.findAll();
        String[] titles = {
                "How to choose the right career path?",
                "Tips for writing an effective resume.",
                "How to prepare for job interviews?",
                "Skills that employers value the most.",
                "How to network effectively as a student?"
        };
        String[] contents = {
                "I am unsure about my career path. Can you guide me?",
                "What are some tips for creating an effective resume?",
                "How do I prepare for job interviews to make a good impression?",
                "What skills do employers look for in fresh graduates?",
                "How can I network effectively while still in school?"
        };
        String[] answers = {
                "Consider your interests, skills, and values. Research different industries.",
                "Tailor your resume to highlight relevant skills and experience for the job.",
                "Practice common interview questions and showcase your strengths.",
                "Employers value communication, problem-solving, and teamwork skills.",
                "Attend career fairs, join student organizations, and connect on LinkedIn."
        };

        createQuestionCards(careerCounselor, students, titles, contents, answers, QuestionType.NON_ACADEMIC, QuestionCard.QuestionCarDifficultyLevel.Hard);
    }

    private void seedQuestionCardForSoftwareEngineeringCounselor() {
        List<Counselor> softwareEngineeringCounselor = academicCounselorRepository.findAll()
                .stream()
                .filter(counselor -> counselor.getMajor().getName().equals("Software Engineering"))
                .map(counselor -> (Counselor) counselor)
                .toList();
//                .findFirst().orElseThrow(() -> new NotFoundException("Not found SE counselor for seeding"));

        List<Student> students = studentRepository.findAll();
        String[] titles = {
                "How to learn object-oriented programming effectively?",
                "What are the best practices for writing clean code?",
                "How to debug code efficiently?",
                "What is the best way to prepare for software engineering exams?",
                "How to manage group projects in software development?"
        };
        String[] contents = {
                "I struggle with understanding object-oriented programming. Can you help?",
                "What are some tips for writing clean and maintainable code?",
                "I often get stuck while debugging. Any advice?",
                "How do I effectively prepare for exams in software engineering?",
                "Group projects are challenging. How can I manage them better?"
        };
        String[] answers = {
                "Start with understanding the four pillars of OOP: encapsulation, inheritance, abstraction, and polymorphism.",
                "Use meaningful variable names, follow SOLID principles, and write unit tests.",
                "Use a systematic approach: isolate the issue, check logs, and use a debugger.",
                "Review past exams, understand key concepts, and practice coding problems.",
                "Divide tasks, set clear deadlines, and use version control to track progress."
        };

        createQuestionCards(softwareEngineeringCounselor, students, titles, contents, answers, QuestionType.ACADEMIC, QuestionCard.QuestionCarDifficultyLevel.Medium);
    }

    private void seedQuestionCardForArtificialIntelligenceCounselor() {
        List<Counselor> artificialIntelligenceCounselor = academicCounselorRepository.findAll()
                .stream()
                .filter(counselor -> counselor.getMajor().getName().equals("Artificial Intelligence"))
                .map(counselor -> (Counselor) counselor)
                .toList();
//                .findFirst().orElseThrow(() -> new NotFoundException("Not found AI counselor for seeding"));

        List<Student> students = studentRepository.findAll();
        String[] titles = {
                "How to get started with machine learning?",
                "What are the best algorithms for classification tasks?",
                "How do I improve the performance of a neural network?",
                "What resources should I use to learn deep learning?",
                "How do I approach artificial intelligence project development?",
                "What are the main characteristics of a neural network?",
                "Describe the phenomenon of overfitting in machine learning.",
                "How does artificial intelligence differ from machine learning?",
                "In what ways is deep learning distinct from traditional machine learning?",
                "What obstacles are faced when applying artificial intelligence to real-world scenarios?",
                "In what ways does artificial intelligence enhance automation in industries?"
        };
        String[] contents = {
                "I'm new to machine learning. Where should I begin?",
                "Which classification algorithms should I focus on?",
                "My neural network isn't performing well. What should I do?",
                "Can you recommend deep learning courses or books?",
                "I need guidance on how to structure an AI project.",
                "What are the main characteristics of a neural network?",
                "Describe the phenomenon of overfitting in machine learning.",
                "How does artificial intelligence differ from machine learning?",
                "In what ways is deep learning distinct from traditional machine learning?",
                "What obstacles are faced when applying artificial intelligence to real-world scenarios?",
                "In what ways does artificial intelligence enhance automation in industries?"
        };
        String[] answers = {
                "Start with the basics: learn about supervised vs unsupervised learning and linear regression.",
                "Focus on algorithms like Logistic Regression, Decision Trees, and Support Vector Machines.",
                "Try tuning hyperparameters, use more data, or experiment with different architectures.",
                "Deep Learning with Python by François Chollet and online courses on platforms like Coursera.",
                "Define the problem, gather and preprocess data, choose the right model, and evaluate its performance.",
                "Characteristics of Neural Networks",
                "Understanding Overfitting in Machine Learning",
                "Distinction Between Artificial Intelligence and Machine Learning",
                "Deep Learning versus Traditional Machine Learning",
                "Challenges in Real-World Implementation of Artificial Intelligence",
                "The Role of Artificial Intelligence in Industrial Automation"
        };

        createQuestionCards(artificialIntelligenceCounselor, students, titles, contents, answers, QuestionType.ACADEMIC, QuestionCard.QuestionCarDifficultyLevel.Medium);
    }

    private void seedQuestionCardForInternationalBusinessCounselor() {
        List<Counselor> internationalBusinessCounselor = academicCounselorRepository.findAll()
                .stream()
                .filter(counselor -> counselor.getMajor().getName().equals("International Business"))
                .map(counselor -> (Counselor) counselor)
                .toList();
//                .findFirst().orElseThrow(() -> new NotFoundException("Not found IB counselor for seeding"));

        List<Student> students = studentRepository.findAll();
        String[] titles = {
                "How do I prepare for a career in international business?",
                "What are the key challenges in managing cross-cultural teams?",
                "How do global economic trends affect international business?",
                "What skills are most important for international business professionals?",
                "How do I succeed in international negotiations?"
        };
        String[] contents = {
                "I'm interested in international business but don't know where to start. Any advice?",
                "How do I manage teams from different cultural backgrounds?",
                "How do I keep track of the latest global economic developments?",
                "Which skills should I focus on developing for a career in international business?",
                "I'm new to international negotiations. How should I prepare?"
        };
        String[] answers = {
                "Start by learning the basics of global trade, economics, and business laws.",
                "Be aware of cultural differences, communicate openly, and foster respect among team members.",
                "Stay updated with global news, attend webinars, and follow key economic indicators.",
                "Focus on communication, cultural awareness, negotiation, and problem-solving skills.",
                "Prepare by understanding the cultural context, being flexible, and building rapport."
        };

        createQuestionCards(internationalBusinessCounselor, students, titles, contents, answers, QuestionType.ACADEMIC, QuestionCard.QuestionCarDifficultyLevel.Medium);
    }

    private void seedQuestionCardForFinanceCounselor() {
        List<Counselor> financeCounselor = academicCounselorRepository.findAll()
                .stream()
                .filter(counselor -> counselor.getMajor().getName().equals("Finance"))
                .map(counselor -> (Counselor) counselor)
                .toList();
//                .findFirst().orElseThrow(() -> new NotFoundException("Not found Finance counselor for seeding"));

        List<Student> students = studentRepository.findAll();
        String[] titles = {
                "How should I start learning about financial analysis?",
                "What are the best strategies for personal financial planning?",
                "How do I approach investment management?",
                "What are the key financial ratios I should focus on?",
                "How can I prepare for a career in corporate finance?"
        };
        String[] contents = {
                "I'm new to finance and need guidance on where to start. Any tips?",
                "What are the main steps in creating a solid financial plan?",
                "How do I begin learning about investment management?",
                "Which financial ratios are essential for evaluating a company?",
                "I'm interested in corporate finance. How can I prepare for this career?"
        };
        String[] answers = {
                "Start by learning the basics of accounting, financial statements, and cash flow analysis.",
                "Begin with setting clear goals, budgeting, saving, and investing wisely.",
                "Understand different types of investments, risk tolerance, and portfolio diversification.",
                "Focus on ratios like the current ratio, return on equity (ROE), and debt-to-equity ratio.",
                "Study corporate financial management, risk analysis, and learn financial modeling techniques."
        };

        createQuestionCards(financeCounselor, students, titles, contents, answers, QuestionType.ACADEMIC, QuestionCard.QuestionCarDifficultyLevel.Medium);
    }

    private void seedAppointment() {
        List<CounselingSlot> slots = getCounselingSlots(); // Assume slots are pre-fetched
        List<Student> students = getStudents(); // Assume students are pre-fetched
        List<Counselor> counselors = getCounselors(); // Assume counselors are pre-fetched

        Random random = new Random();
        List<CounselingAppointmentRequest> requests = new ArrayList<>();
        List<CounselingAppointment> appointments = new ArrayList<>();
        List<AppointmentFeedback> feedbacks = new ArrayList<>();

        int[] absentCounts = {0, 0, 0}; // Track absent count for each month

        // Create 50 requests
        for (int i = 0; i < 50; i++) {
            // Determine status (3 denies per month)
            boolean isDenied = (i % 17 == 0) || (i % 18 == 0) || (i % 19 == 0); // Spread 3 denies each month
            CounselingAppointmentRequestStatus status = isDenied ? CounselingAppointmentRequestStatus.DENIED : CounselingAppointmentRequestStatus.APPROVED;
            MeetingType meetingType = (i % 3 == 0) ? MeetingType.OFFLINE : MeetingType.ONLINE;

            // Randomize request dates across 3 months
            int monthIndex = i < 20 ? 0 : (i < 35 ? 1 : 2);
            LocalDate baseDate = LocalDate.now().minusMonths(monthIndex);
            LocalDate requestDate = baseDate.minusDays(random.nextInt(30));

            CounselingAppointmentRequest request = CounselingAppointmentRequest.builder()
                    .requireDate(requestDate)
                    .startTime(slots.get(i % slots.size()).getStartTime())
                    .endTime(slots.get(i % slots.size()).getEndTime())
                    .status(status)
                    .meetingType(meetingType)
                    .reason("Reason for request " + (i + 1))
                    .counselor(counselors.get(random.nextInt(8)))
                    .student(students.get(random.nextInt(15)))
                    .build();
            requests.add(request);

            // Create appointments only for approved requests
            if (status == CounselingAppointmentRequestStatus.APPROVED) {
                CounselingAppointment appointment;
                if (meetingType == MeetingType.ONLINE) {
                    appointment = OnlineAppointment.builder()
                            .meetUrl("https://meet.example.com/session" + i)
                            .build();
                } else {
                    appointment = OfflineAppointment.builder()
                            .address("Room " + (i + 1))
                            .build();
                }

                appointment.setStartDateTime(LocalDateTime.of(request.getRequireDate(), request.getStartTime()));
                appointment.setEndDateTime(LocalDateTime.of(request.getRequireDate(), request.getEndTime()));
                appointment.setMeetingType(request.getMeetingType());
                appointment.setCounselor(request.getCounselor());
                appointment.setStudent(request.getStudent());
                appointment.setReason(request.getReason());

                // Set status: 2 absent per month, rest are attend
                if (absentCounts[monthIndex] < 2) {
                    appointment.setStatus(CounselingAppointmentStatus.ABSENT);
                    absentCounts[monthIndex]++;
                } else {
                    appointment.setStatus(CounselingAppointmentStatus.ATTEND);
                    // Create and set an AppointmentReport for the appointment
                    AppointmentReport report = AppointmentReport.builder()
                            .interventionType("Initial Consultation")  // Example field value
                            .interventionDescription("Discussed student's challenges and goals.")  // Example field value
                            .counselorConclusion("Student needs follow-up consultation.")  // Example field value
                            .followUpNeeded(true)  // Example boolean value
                            .followUpNotes("Student will be contacted in 2 weeks.")  // Example field value
                            .summaryOfDiscussion("The student expressed concerns about their academic performance.")  // Example field value
                            .mainIssues("Time management and stress management.")  // Example field value
                            .studentEmotions("Anxious, overwhelmed.")  // Example field value
                            .studentReactions("Nervous but open to guidance.")  // Example field value
                            .specificGoal("Improve time management skills.")  // Example field value
                            .reason("Student expressed difficulty with balancing study and personal life.")  // Example field value
                            .student(request.getStudent())
                            .counselor(request.getCounselor())
                            .counselingAppointment(appointment)  // Link the report to the appointment
                            .build();

                    appointment.setReport(report);
                }

                appointment.setAppointmentRequest(request);
                appointments.add(appointment);

                // Generate feedback for the appointment
                BigDecimal rating = BigDecimal.valueOf(1 + random.nextInt(5));
                String comment = switch (rating.intValue()) {
                    case 1 -> "Very dissatisfied";
                    case 2 -> "Dissatisfied";
                    case 3 -> "Neutral";
                    case 4 -> "Satisfied";
                    case 5 -> "Very satisfied";
                    default -> "No comment";
                };

                AppointmentFeedback feedback = AppointmentFeedback.builder()
                        .rating(rating)
                        .comment(comment)
                        .appointment(appointment)
                        .counselor(request.getCounselor())
                        .build();
                feedbacks.add(feedback);
            }
        }

        saveRequests(requests);
        saveAppointments(appointments);
        saveFeedbacks(feedbacks);
    }

    private List<CounselingSlot> getCounselingSlots() {
        return counselingSlotRepository.findAll();
    }

    private List<Student> getStudents() {
        return studentRepository.findAll();
    }

    private List<Counselor> getCounselors() {
        return counselorRepository.findAll();
    }

    private void saveRequests(List<CounselingAppointmentRequest> requests) {
        counselingAppointmentRequestRepository.saveAll(requests);
    }

    private void saveAppointments(List<CounselingAppointment> appointments) {
        counselingAppointmentRepository.saveAll(appointments);
    }

    private final AppointmentFeedbackRepository appointmentFeedbackRepository;

    private void saveFeedbacks(List<AppointmentFeedback> feedbacks) {
        appointmentFeedbackRepository.saveAll(feedbacks);
    }

}

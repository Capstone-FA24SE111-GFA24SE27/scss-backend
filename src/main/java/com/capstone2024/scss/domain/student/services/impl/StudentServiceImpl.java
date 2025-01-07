package com.capstone2024.scss.domain.student.services.impl;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.student.dto.*;
import com.capstone2024.scss.application.student.dto.enums.AttendanceStatus;
import com.capstone2024.scss.domain.common.entity.Semester;
import com.capstone2024.scss.domain.common.helpers.StudentFilterHelper;
import com.capstone2024.scss.domain.common.mapper.account.AcademicDepartmentDetailMapper;
import com.capstone2024.scss.domain.common.mapper.account.ProfileMapper;
import com.capstone2024.scss.domain.demand.entities.DemandProblemTag;
import com.capstone2024.scss.domain.demand.entities.ProblemTag;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.entities.StudentCounselingProfile;
import com.capstone2024.scss.domain.student.entities.academic.AcademicTranscript;
import com.capstone2024.scss.domain.student.entities.academic.AttendanceDetail;
import com.capstone2024.scss.domain.student.entities.academic.StudentStudy;
import com.capstone2024.scss.domain.student.entities.academic.enums.StudyStatus;
import com.capstone2024.scss.domain.student.enums.CounselingProfileStatus;
import com.capstone2024.scss.domain.student.services.StudentService;
import com.capstone2024.scss.domain.common.mapper.student.StudentMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.infrastructure.configuration.openai.OpenAIService;
import com.capstone2024.scss.infrastructure.configuration.openai.dto.SubjectOpenAi;
import com.capstone2024.scss.infrastructure.data.fap.dto.SemesterFapResponseDTO;
import com.capstone2024.scss.infrastructure.repositories.SemesterRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.DemandProblemTagRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.ProblemTagRepository;
import com.capstone2024.scss.infrastructure.repositories.student.CounselingProfileRepository;
import com.capstone2024.scss.infrastructure.repositories.student.StudentRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingAppointmentRepository;
import com.capstone2024.scss.infrastructure.repositories.student.academic.AcademicTranscriptRepository;
import com.capstone2024.scss.infrastructure.repositories.student.academic.AttendanceDetailRepository;
import com.capstone2024.scss.infrastructure.repositories.student.academic.StudentStudyRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);
    private final StudentRepository studentRepository;
    private final CounselingAppointmentRepository appointmentRepository;
    private final CounselingProfileRepository counselingProfileRepository;
    private final RestTemplate restTemplate;
    private final SemesterRepository semesterRepository;
    private final DemandProblemTagRepository demandProblemTagRepository;
    private final ProblemTagRepository problemTagRepository;
    private final OpenAIService openAIService;
    private final StudentStudyRepository studentStudyRepository;
    private final AttendanceDetailRepository attendanceDetailRepository;
    private final AcademicTranscriptRepository academicTranscriptRepository;

    @Value("${server.api.fap.system.base.url}")
    private String fapServerUrl;

    @Override
    public StudentProfileDTO getStudentById(Long id) {
        Student student = studentRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Student with ID:" + id + " not found"));
        return StudentMapper.toStudentProfileDTO(student);
    }

    @Override
    public StudentProfileDTO getStudentByStudentCode(String studentCode) {
        Student student = studentRepository
                .findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Student with code: " + studentCode + " not found"));

        return StudentMapper.toStudentProfileDTO(student);
    }

    @Override
    public void createCounselingProfile(StudentCounselingProfileRequestDTO requestDTO, Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + studentId));

        // Tạo đối tượng StudentCounselingProfile mới
        StudentCounselingProfile profile = counselingProfileRepository.findByStudent(student)
                .orElse(new StudentCounselingProfile());
        profile.setIntroduction(requestDTO.getIntroduction());
        profile.setCurrentHealthStatus(requestDTO.getCurrentHealthStatus());
        profile.setPsychologicalStatus(requestDTO.getPsychologicalStatus());
        profile.setStressFactors(requestDTO.getStressFactors());
        profile.setAcademicDifficulties(requestDTO.getAcademicDifficulties());
        profile.setStudyPlan(requestDTO.getStudyPlan());
        profile.setCareerGoals(requestDTO.getCareerGoals());
        profile.setPartTimeExperience(requestDTO.getPartTimeExperience());
        profile.setInternshipProgram(requestDTO.getInternshipProgram());
        profile.setExtracurricularActivities(requestDTO.getExtracurricularActivities());
        profile.setPersonalInterests(requestDTO.getPersonalInterests());
        profile.setSocialRelationships(requestDTO.getSocialRelationships());
        profile.setFinancialSituation(requestDTO.getFinancialSituation());
        profile.setFinancialSupport(requestDTO.getFinancialSupport());
//        profile.setCounselingIssue(requestDTO.getCounselingIssue());
//        profile.setCounselingGoal(requestDTO.getCounselingGoal());
        profile.setDesiredCounselingFields(requestDTO.getDesiredCounselingFields());
        profile.setStatus(CounselingProfileStatus.UNVERIFIED); // Trạng thái mặc định

        // Thiết lập Student cho CounselingProfile
        student.setCounselingProfile(profile);

        // Lưu CounselingProfile vào cơ sở dữ liệu
        studentRepository.save(student);
    }

    @Override
    public void updateCounselingProfile(StudentCounselingProfileRequestDTO requestDTO, Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + studentId));

        // Tìm hồ sơ tư vấn hiện tại
        StudentCounselingProfile profile = counselingProfileRepository.findByStudent(student)
                .orElseThrow(() -> new NotFoundException("Counseling profile not found for student with id " + studentId));

        // Cập nhật thông tin cho hồ sơ tư vấn
        profile.setIntroduction(requestDTO.getIntroduction());
        profile.setCurrentHealthStatus(requestDTO.getCurrentHealthStatus());
        profile.setPsychologicalStatus(requestDTO.getPsychologicalStatus());
        profile.setStressFactors(requestDTO.getStressFactors());
        profile.setAcademicDifficulties(requestDTO.getAcademicDifficulties());
        profile.setStudyPlan(requestDTO.getStudyPlan());
        profile.setCareerGoals(requestDTO.getCareerGoals());
        profile.setPartTimeExperience(requestDTO.getPartTimeExperience());
        profile.setInternshipProgram(requestDTO.getInternshipProgram());
        profile.setExtracurricularActivities(requestDTO.getExtracurricularActivities());
        profile.setPersonalInterests(requestDTO.getPersonalInterests());
        profile.setSocialRelationships(requestDTO.getSocialRelationships());
        profile.setFinancialSituation(requestDTO.getFinancialSituation());
        profile.setFinancialSupport(requestDTO.getFinancialSupport());
//        profile.setCounselingIssue(requestDTO.getCounselingIssue());
//        profile.setCounselingGoal(requestDTO.getCounselingGoal());
        profile.setDesiredCounselingFields(requestDTO.getDesiredCounselingFields());

        // Lưu hồ sơ tư vấn đã cập nhật vào cơ sở dữ liệu
        counselingProfileRepository.save(profile);
    }

    @Override
    public List<StudyDTO> getStudiesByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + studentId));

        List<AcademicTranscript> academicTranscripts = academicTranscriptRepository.findByStudent(student);
        return academicTranscripts.stream()
                .map(academicTranscript -> StudyDTO.builder()
                        .grade(academicTranscript.getGrade())
                        .term(academicTranscript.getTerm())
                        .subjectCode(academicTranscript.getSubjectCode())
                        .subjectName(academicTranscript.getSubjectName())
                        .status(academicTranscript.getStatus().name())
                        .semester(academicTranscript.getSemester().getName())
                        .build())
                .collect(Collectors.toList());

//        String url = fapServerUrl + "/api/studies/" + student.getStudentCode();
//        StudyDTO[] studiesArray = restTemplate.getForObject(url, StudyDTO[].class);
//        if(studiesArray == null) {
//            return new ArrayList<>();
//        }
//        return List.of(studiesArray);
    }

    @Override
    public List<AttendanceDTO> getAttendanceByStudentCodeAndSemesterName(Long studentId, String semesterName) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + studentId));

        Semester semester = semesterRepository.findByName(semesterName)
                .orElseThrow(() -> new NotFoundException("Semester not found with name: " + semesterName));

//        String url = fapServerUrl + "/api/students/" + student.getStudentCode() + "/semester/" + semesterName;
//        AttendanceDTO[] attendanceDTOS = restTemplate.getForObject(url, AttendanceDTO[].class);
//        if(attendanceDTOS == null) {
//            return new ArrayList<>();
//        }
//        return List.of(attendanceDTOS);
        List<StudentStudy> attendances = studentStudyRepository.findByStudent_StudentCodeAndSemester_Name(student.getStudentCode(), semesterName);
        return attendances.stream()
                .map(attendance -> AttendanceDTO.builder()
                        .id(attendance.getId())
                        .startDate(attendance.getStartDate())
                        .grade(attendance.getFinalGrade())
                        .totalSlot(attendance.getTotalSlot())
                        .studentCode(attendance.getStudent().getStudentCode())
                        .subjectName(attendance.getSubjectName()) // giả sử Subject có thuộc tính name
                        .semesterName(attendance.getSemester().getName())
                        .detais(attendance.getAttendanceDetails().stream().map(this::toDetailDTO).collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    private AttendanceDetailDTO toDetailDTO(AttendanceDetail detail) {
        return AttendanceDetailDTO.builder()
                .date(detail.getDate())
                .slot(detail.getSlot())
                .room(detail.getRoom())
                .lecturer(detail.getLecturer())
                .groupName(detail.getGroupName())
                .status(AttendanceStatus.valueOf(detail.getStatus().name()))
                .lecturerComment(detail.getLecturerComment())
                .build();
    }

    @Override
    public List<AttendanceDetailDTO> getAttendanceDetailsByStudentCodeAndAttendanceId(Long studentId, Long attendanceId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + studentId));

        List<AttendanceDetail> attendanceDetails = attendanceDetailRepository.findByStudentStudy_IdAndStudentStudy_Student_StudentCode(attendanceId, student.getStudentCode());

        return attendanceDetails.stream()
                .map(this::toDetailDTO)
                .collect(Collectors.toList());
//        String url = fapServerUrl + "/api/students/" + student.getStudentCode() + "/attendance/" + attendanceId;
//        AttendanceDetailDTO[] attendanceDTOS = restTemplate.getForObject(url, AttendanceDetailDTO[].class);
//        if(attendanceDTOS == null) {
//            return new ArrayList<>();
//        }
//        return List.of(attendanceDTOS);
    }

    public static List<SubjectOpenAi> parseBehaviorTags(Map<String, List<DemandProblemTagResponseDTO>> input) {
        List<SubjectOpenAi> subjectOpenAiList = new ArrayList<>();

        // Iterate over each subject and its related behavior tags
        for (Map.Entry<String, List<DemandProblemTagResponseDTO>> entry : input.entrySet()) {
            String subjectName = entry.getKey();
            List<DemandProblemTagResponseDTO> tagsList = entry.getValue();

            // Create a map to count the frequency of each behavior tag
            Map<String, Integer> behaviorTagCount = new HashMap<>();

            // Iterate through the list of DemandProblemTagResponseDTO to populate the behavior tag counts
            for (DemandProblemTagResponseDTO dto : tagsList) {
                String tagName = dto.getProblemTagName(); // Get the tag name
                behaviorTagCount.put(tagName, behaviorTagCount.getOrDefault(tagName, 0) + dto.getNumber()); // Increment the count
            }

            // Create the SubjectOpenAi object for each subject
            SubjectOpenAi subjectOpenAi = SubjectOpenAi.builder()
                    .name(subjectName)
                    .behaviorTags(behaviorTagCount)
                    .build();

            // Add the subject to the list
            subjectOpenAiList.add(subjectOpenAi);
        }

        return subjectOpenAiList;
    }

    // Method to check if the list is empty or if all behaviorTags are empty
    public static boolean isValidSubjectOpenAiList(List<SubjectOpenAi> subjectOpenAiList) {
        // Check if the list is empty
        if (subjectOpenAiList == null || subjectOpenAiList.isEmpty()) {
            return false;
        }

        // Iterate through each SubjectOpenAi and check if behaviorTags is empty
        for (SubjectOpenAi subject : subjectOpenAiList) {
            if (subject.getBehaviorTags() == null || subject.getBehaviorTags().isEmpty()) {
                // If behaviorTags is empty or null for any subject, return false
                return false;
            }
        }

        // If none of the above conditions are met, return true (valid list)
        return true;
    }

    @Override
    public String getGeneralAssessment(Long studentId, String semesterName) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + studentId));

        Semester semester = semesterRepository.findByName(semesterName)
                .orElseThrow(() -> new NotFoundException("Semester not found with name: " + semesterName));

        List<DemandProblemTag> demandProblemTags = demandProblemTagRepository.findByStudentIdAndSemesterId(studentId, semester.getId());

        List<DemandProblemTagResponseDTO> demandProblemTagResponseDTOS = demandProblemTags.stream().map(demandProblemTag -> {
            return DemandProblemTagResponseDTO.builder()
                    .category(demandProblemTag.getProblemTag().getCategory().getName())
                    .source(demandProblemTag.getSource())
                    .problemTagName(demandProblemTag.getProblemTag().getName())
                    .isExcluded(demandProblemTag.isExcluded())
                    .build();
        }).collect(Collectors.toList());

        Map<String, List<DemandProblemTagResponseDTO>> rawSubjectWithBehaviorTag = demandProblemTagResponseDTOS.stream()
                .collect(Collectors.groupingBy(
                        DemandProblemTagResponseDTO::getSource
                ));

        Map<String, List<DemandProblemTagResponseDTO>> result = new HashMap<>();

        rawSubjectWithBehaviorTag.forEach((key, list) -> result.put(key, mergeByProblemTagName(list, false)));

        List<SubjectOpenAi> subjectOpenAiList = parseBehaviorTags(result);
        if(!isValidSubjectOpenAiList(subjectOpenAiList)) {
            return "Nothing to assess";
        }
        String prompt = openAIService.generatePromptForGeneralAssessment(subjectOpenAiList);
        return openAIService.callOpenAPIForGeneralAssessment(prompt);
    }

    @Override
    public Object getDemandProblemTagDetailByStudentAndSemester(Long studentId, String semesterName) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + studentId));

        Semester semester = semesterRepository.findByName(semesterName)
                .orElseThrow(() -> new NotFoundException("Semester not found with name: " + semesterName));

        List<DemandProblemTag> demandProblemTags = demandProblemTagRepository.findByStudentIdAndSemesterId(studentId, semester.getId());

        List<DemandProblemTagResponseDTO> demandProblemTagResponseDTOS = demandProblemTags.stream().map(demandProblemTag -> {
            return DemandProblemTagResponseDTO.builder()
                    .category(demandProblemTag.getProblemTag().getCategory().getName())
                    .source(demandProblemTag.getSource())
                    .problemTagName(demandProblemTag.getProblemTag().getName())
                    .isExcluded(demandProblemTag.isExcluded())
                    .build();
        }).collect(Collectors.toList());

        Map<String, Map<String, List<DemandProblemTagResponseDTO>>>  res = demandProblemTagResponseDTOS.stream()
                .collect(Collectors.groupingBy(
                        DemandProblemTagResponseDTO::getSource, // Group by source
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(dto -> dto.isExcluded() ? "isExcluded" : "isNotExcluded"),
                                map -> {
                                    Map<String, List<DemandProblemTagResponseDTO>> result = new HashMap<>();
                                    // Add both keys with empty lists by default
                                    result.put("isExcluded", new ArrayList<>());
                                    result.put("isNotExcluded", new ArrayList<>());

                                    // Populate result map with merged lists
                                    map.forEach((key, list) -> result.put(key, mergeByProblemTagName(list, false)));
                                    return result;
                                }
                        )
                ));

        return res;

    }

    @Override
    public PaginationDTO<List<StudentDetailForFilterDTO>> getStudentsWithRecommend(StudentFilterRequestDTO filterRequest) {
        List<List<String>> listOfListsStudentCode = new ArrayList<>();
        Page<Student> studentPage = null;
        List<StudentDetailForFilterDTO> studentDTOs = null;
//        if(filterRequest.isUsingPrompt()) {
        List<String> tagList = filterRequest.isUsingPrompt() ?
                parseTagList(filterRequest.getBehaviorOption() != null ? filterRequest.getBehaviorOption().getPrompt() : null)
                :
                filterRequest.getBehaviorOption().getBehaviorList();
        studentPage = studentRepository.findStudentsByProblemTagsRecommend(
                filterRequest.getAcademicOption().getSpecializationId(),
                filterRequest.getKeyword(),
                filterRequest.getAcademicOption().getDepartmentId(),
                filterRequest.getAcademicOption().getMajorId(),
//                tagList,
                filterRequest.getBehaviorOption().getSemesterId(), filterRequest.getPagination());

        listOfListsStudentCode.add(studentPage.getContent().stream().map(Student::getStudentCode).collect(Collectors.toList()));

//        if(filterRequest.getAttendanceAsCountOption() != null && filterRequest.getAttendanceAsCountOption().getSemesterId() != null) {
//            listOfListsStudentCode.add(getAttendanceCountFilter(filterRequest.getAttendanceAsCountOption()));
//        }
//
//        if(filterRequest.getAttendanceAsPercentOption() != null && filterRequest.getAttendanceAsPercentOption().getSemesterId() != null) {
//            listOfListsStudentCode.add(getAttendancePercentageFilter(filterRequest.getAttendanceAsPercentOption()));
//        }
//
//        if(filterRequest.getGpaOption().getSemesterId() != null) {
//            listOfListsStudentCode.add(getGPAFilter(filterRequest.getGpaOption()));
//        }

        List<String> joinedStudentCode = getUniqueElements(listOfListsStudentCode);
        List<String> joinedStudentCodePagination = getPage(joinedStudentCode, filterRequest.getPage() - 1, filterRequest.getSize());

        List<Student> allStudent = studentRepository.findAll();

        List<Student> studentsResponse = filterStudentsByCode(allStudent, joinedStudentCodePagination);

        studentDTOs = studentsResponse.stream()
                .map(student -> toStudentDetailDTO(student, tagList == null ? new ArrayList<>() : tagList, filterRequest, true))
                .collect(Collectors.toList());
//        } else {
//            studentPage = studentRepository.findStudents(
//                    filterRequest.getAcademicOption().getSpecializationId(),
//                    filterRequest.getKeyword(),
//                    filterRequest.getAcademicOption().getDepartmentId(),
//                    filterRequest.getAcademicOption().getMajorId(),
//                    filterRequest.getPagination());
//            studentDTOs = studentPage.getContent().stream()
//                    .map(student -> toStudentDetailDTO(student, null, filterRequest, false))
//                    .collect(Collectors.toList());
//        }
        return PaginationDTO.<List<StudentDetailForFilterDTO>>builder()
                .data(studentDTOs)
                .totalPages((joinedStudentCode.size() + 10 - 1) / 10)
                .totalElements(joinedStudentCode.size())
                .build();
    }

    @Override
    public void excludeAllDemandProblemTagsByStudentId(Long studentId) {
        List<DemandProblemTag> demandProblemTags = demandProblemTagRepository.findByStudentIdAndIsExcludedFalse(studentId, false);

        // Update all tags to have isExcluded = true
        demandProblemTags.forEach(tag -> tag.setExcluded(true));

        // Save all updated tags
        demandProblemTagRepository.saveAll(demandProblemTags);
    }

    @Override
    public StudentDocumentDTO getStudentDocumentById(Long studentId) {
        Student student = studentRepository
                .findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student with ID:" + studentId + " not found"));

        List<CounselingAppointment> appointments = appointmentRepository.findAllByStudentId(studentId);

        return StudentMapper.toStudentDocumentDTO(student, appointments);
    }

    @Override
    public PaginationDTO<List<StudentDetailForFilterDTO>> getStudents(StudentFilterRequestDTO filterRequest) {
        List<List<String>> listOfListsStudentCode = new ArrayList<>();
        Page<Student> studentPage = null;
        List<StudentDetailForFilterDTO> studentDTOs = null;
//        if(filterRequest.isUsingPrompt()) {
            List<String> tagList = filterRequest.isUsingPrompt() ?
                    parseTagList(filterRequest.getBehaviorOption() != null ? filterRequest.getBehaviorOption().getPrompt() : null)
                    :
                    filterRequest.getBehaviorOption().getBehaviorList();
            studentPage = studentRepository.findStudentsByProblemTagsAndOptionalSemester(
                    filterRequest.getAcademicOption().getSpecializationId(),
                    filterRequest.getKeyword(),
                    filterRequest.getAcademicOption().getDepartmentId(),
                    filterRequest.getAcademicOption().getMajorId(),
                    tagList,
                    filterRequest.getBehaviorOption().getSemesterId(), filterRequest.getPagination());

            listOfListsStudentCode.add(studentPage.getContent().stream().map(Student::getStudentCode).collect(Collectors.toList()));

            if(filterRequest.getAttendanceAsCountOption() != null && filterRequest.getAttendanceAsCountOption().getSemesterId() != null) {
                listOfListsStudentCode.add(getAttendanceCountFilter(filterRequest.getAttendanceAsCountOption()));
            }

            if(filterRequest.getAttendanceAsPercentOption() != null && filterRequest.getAttendanceAsPercentOption().getSemesterId() != null) {
                listOfListsStudentCode.add(getAttendancePercentageFilter(filterRequest.getAttendanceAsPercentOption()));
            }

            if(filterRequest.getGpaOption().getSemesterId() != null) {
                listOfListsStudentCode.add(getGPAFilter(filterRequest.getGpaOption()));
            }

            List<String> joinedStudentCode = findCommonStrings(listOfListsStudentCode);
            List<String> joinedStudentCodePagination = getPage(joinedStudentCode, filterRequest.getPage() - 1, filterRequest.getSize());

            List<Student> studentsResponse = filterStudentsByCode(studentPage.getContent(), joinedStudentCodePagination);

            studentDTOs = studentsResponse.stream()
                    .map(student -> toStudentDetailDTO(student, tagList == null ? new ArrayList<>() : tagList, filterRequest, false))
                    .collect(Collectors.toList());
//        } else {
//            studentPage = studentRepository.findStudents(
//                    filterRequest.getAcademicOption().getSpecializationId(),
//                    filterRequest.getKeyword(),
//                    filterRequest.getAcademicOption().getDepartmentId(),
//                    filterRequest.getAcademicOption().getMajorId(),
//                    filterRequest.getPagination());
//            studentDTOs = studentPage.getContent().stream()
//                    .map(student -> toStudentDetailDTO(student, null, filterRequest, false))
//                    .collect(Collectors.toList());
//        }
        return PaginationDTO.<List<StudentDetailForFilterDTO>>builder()
                .data(studentDTOs)
                .totalPages((joinedStudentCode.size() + 10 - 1) / 10)
                .totalElements(joinedStudentCode.size())
                .build();
    }

    private String getSemesterCodeById(Long semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new NotFoundException("Semester not found"));

        return semester.getName();
    }

    private List<String> getAttendanceCountFilter(StudentAttendanceAsCountFilterDTO filterRequest) {
        String semester = getSemesterCodeById(filterRequest.getSemesterId());

//        String url = StudentFilterHelper.buildAttendanceCountFilterURL(fapServerUrl, semester, filterRequest);
//
//        ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
//        String[] studentCode = response.getBody();

        List<Student> students = studentRepository.findStudentsWithAbsenceCountRange(
                semester,
                Long.valueOf(filterRequest.getFrom()),
                Long.valueOf(filterRequest.getTo()),
                Long.valueOf(filterRequest.getMinSubject())
        );

        return students.stream()
                .map(Student::getStudentCode)
                .collect(Collectors.toList());

//        if (studentCode != null) {
//            return Arrays.asList(studentCode);
//        } else {
//            return new ArrayList<>();
//        }
    }

    private List<String> getAttendancePercentageFilter(StudentAttendanceAsPercentFilterDTO filterRequest) {
        String semester = getSemesterCodeById(filterRequest.getSemesterId());

//        String url = StudentFilterHelper.buildAttendancePercentageFilterURL(fapServerUrl, semester, filterRequest);
//
//        ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
//        String[] studentCode = response.getBody();
//
//        if (studentCode != null) {
//            return Arrays.asList(studentCode);
//        } else {
//            return new ArrayList<>();
//        }
        List<Student> students = studentRepository.findStudentsWithAbsencePercentageRange(
                semester,
                filterRequest.getFrom(),
                filterRequest.getTo(),
                Long.valueOf(filterRequest.getMinSubject())
        );

        return students.stream()
                .map(Student::getStudentCode)
                .collect(Collectors.toList());
    }

    private List<String> getGPAFilter(StudentGPAFilterDTO filterRequest) {
        String semester = getSemesterCodeById(filterRequest.getSemesterId());
//
//        String url = StudentFilterHelper.buildGPAFilterURL(fapServerUrl, semester, filterRequest);
//
//        ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
//        String[] studentCode = response.getBody();
//
//        if (studentCode != null) {
//            return Arrays.asList(studentCode);
//        } else {
//            return new ArrayList<>();
//        }
        List<Student> students = studentRepository.findStudentsWithGPA(
                semester,
                filterRequest.getMin().doubleValue(),
                filterRequest.getMax().doubleValue()
        );

        return students.stream()
                .map(Student::getStudentCode)
                .collect(Collectors.toList());
    }

    private List<Student> filterStudentsByCode(List<Student> studentPage, List<String> joinedStudentCode) {
        // Kiểm tra null hoặc danh sách rỗng
        if (studentPage == null || joinedStudentCode == null || joinedStudentCode.isEmpty()) {
            return Collections.emptyList();
        }

        // Lọc danh sách Student dựa trên studentCode có trong joinedStudentCode
        return studentPage.stream()
                .filter(student -> joinedStudentCode.contains(student.getStudentCode()))
                .collect(Collectors.toList());
    }

    private List<String> getPage(List<String> list, int page, int size) {
        // Kiểm tra danh sách null hoặc trống
        if (list == null || list.isEmpty() || size <= 0 || page < 0) {
            return Collections.emptyList();
        }

        // Tính toán chỉ số bắt đầu và kết thúc
        int start = page * size;
        int end = Math.min(start + size, list.size());

        // Nếu chỉ số bắt đầu vượt quá kích thước danh sách, trả về danh sách rỗng
        if (start >= list.size()) {
            return Collections.emptyList();
        }

        // Trả về danh sách con
        return list.subList(start, end);
    }

    private List<String> findCommonStrings(List<List<String>> listOfLists) {
        // Kiểm tra trường hợp danh sách trống
        if (listOfLists == null || listOfLists.isEmpty()) {
            return Collections.emptyList();
        }

        // Khởi tạo tập hợp chung từ danh sách đầu tiên
        Set<String> commonStrings = new HashSet<>(listOfLists.get(0));

        // Duyệt qua các danh sách còn lại và giữ lại phần tử chung
        for (int i = 1; i < listOfLists.size(); i++) {
            commonStrings.retainAll(listOfLists.get(i));
        }

        // Chuyển tập hợp kết quả thành danh sách và trả về
        return new ArrayList<>(commonStrings);
    }

    private List<String> getUniqueElements(List<List<String>> listOfLists) {
        Set<String> uniqueElementsSet = new HashSet<>();

        // Duyệt qua từng List trong listOfLists và thêm các phần tử vào Set
        for (List<String> list : listOfLists) {
            uniqueElementsSet.addAll(list);
        }

        // Chuyển Set thành List để trả về
        return new ArrayList<>(uniqueElementsSet);
    }

    private String generatePromptToOpenAI(String prompt) {
        List<String> predefinedTags = problemTagRepository.findAll()
                .stream()
                .map(ProblemTag::getName)
                .collect(Collectors.toList());

        String preDefinedTags = String.join(", ", predefinedTags);

        String promptCommand = "Pre-defined tags: [" + preDefinedTags + "]\n" +
                "Prompt: ["+ prompt + "]\n" +
                "Your action: [\n" +
                "Content Analysis: Before tagging, analyze the prompt to identify its meaning, sentiment, and specific behaviors related to the student.\n" +
                "Filter Out Stop Words: Remove unnecessary stop words to simplify the sentence and focus on key terms related to behavior and academic performance.\n" +
                "Match with Available Tags: Based on the filtered keywords, compare them with the available tags to find the most relevant ones.\n" +
                "Assign Multiple Tags if Necessary: In cases where multiple aspects of behavior are described, multiple tags may be assigned, with each tag representing a specific negative behavior or area of weakness.\n" +
                "If no tags match: If the prompt does not clearly match any of the pre-defined tags, return an empty response or a default tag indicating no relevant tags found.\n" +
                "]\n" +
                "Response: [JSON format with key (result) and value (String array of tags)]";

//        System.out.println(promptCommand);
        return promptCommand;
    }

    public List<String> parseTagList(String input) {
        if(isNullOrIsEmptyString(input)) {
            return null;
        }
        String openAICommand = openAIService.generatePromptToOpenAIForParseBehaviorTag(input);
        String tagListInStringRs = openAIService.callOpenAPIForParseBehaviorTag(openAICommand);
        if(isNullOrIsEmptyString(tagListInStringRs)) {
            return null;
        }
        return Arrays.asList(tagListInStringRs.split(",\\s*"));
    }

    private boolean isNullOrIsEmptyString(String input) {
        if(input == null) {
            return true;
        }
        return input.isEmpty();
    }

    public StudentDetailForFilterDTO toStudentDetailDTO(Student student, List<String> tagList, StudentFilterRequestDTO filterRequestDTO, boolean isRecommend) {
        if (student == null) {
            return null;
        }

        StudentDetailForFilterDTO studentDetailForFilterDTO = StudentDetailForFilterDTO.builder()
                .id(student.getId())
                .profile(ProfileMapper.toProfileDTO(student))
                .studentCode(student.getStudentCode())
                .email(student.getAccount().getEmail())
                .specialization(AcademicDepartmentDetailMapper.toSpecializationDTO(student.getSpecialization()))
                .department(AcademicDepartmentDetailMapper.toDepartmentDTO(student.getDepartment()))
                .major(AcademicDepartmentDetailMapper.toMajorDTO(student.getMajor()))
                .build();

//        if(filterRequestDTO.isIncludeBehavior()) {
            List<DemandProblemTag> demandProblemTags = demandProblemTagRepository.findByStudentIdAndSemesterId(student.getId(), filterRequestDTO.getBehaviorOption().getSemesterId());

            List<DemandProblemTagResponseDTO> demandProblemTagResponseDTOS = demandProblemTags.stream().map(demandProblemTag -> {
                boolean isContained = tagList.stream().anyMatch(tag -> tag.equals(demandProblemTag.getProblemTag().getName()));
                return DemandProblemTagResponseDTO.builder()
                        .category(demandProblemTag.getProblemTag().getCategory().getName())
                        .isExcluded(demandProblemTag.isExcluded())
                        .problemTagName(demandProblemTag.getProblemTag().getName())
                        .isContained(isContained)
                        .build();
            }).collect(Collectors.toList());

            studentDetailForFilterDTO.setBehaviorTagList(mergeByProblemTagName(demandProblemTagResponseDTOS, isRecommend));
//        } else {
//            studentDetailForFilterDTO.setBehaviorTagList(new ArrayList<>());
//        }

        return studentDetailForFilterDTO;
    }

    private List<DemandProblemTagResponseDTO> mergeByProblemTagName(List<DemandProblemTagResponseDTO> inputList, boolean isRecommend) {
        Map<String, DemandProblemTagResponseDTO> mergedMap = new HashMap<>();

        if(!isRecommend) {
            for (DemandProblemTagResponseDTO dto : inputList) {
                String problemTagName = dto.getProblemTagName();

                // Nếu problemTagName đã tồn tại trong mergedMap, cộng dồn number
                if (mergedMap.containsKey(problemTagName)) {
                    DemandProblemTagResponseDTO existingDTO = mergedMap.get(problemTagName);
                    existingDTO.setNumber(existingDTO.getNumber() + 1);
                } else {
                    // Nếu chưa tồn tại, thêm đối tượng vào mergedMap
                    dto.setNumber(1);
                    mergedMap.put(problemTagName, dto);
                }
            }
        } else {
            for (DemandProblemTagResponseDTO dto : inputList) {
                String problemTagName = dto.getProblemTagName();

                // Nếu problemTagName đã tồn tại trong mergedMap, cộng dồn number
                if (mergedMap.containsKey(problemTagName)) {
                    DemandProblemTagResponseDTO existingDTO = mergedMap.get(problemTagName);
                    existingDTO.setNumber(existingDTO.getNumber() + 1);
                } else if (!mergedMap.containsKey(problemTagName) && !dto.isExcluded()) {
                    // Nếu chưa tồn tại, thêm đối tượng vào mergedMap
                    dto.setNumber(1);
                    mergedMap.put(problemTagName, dto);
                }
            }
        }

        // Chuyển kết quả từ map sang list
        return new ArrayList<>(mergedMap.values());
    }
}

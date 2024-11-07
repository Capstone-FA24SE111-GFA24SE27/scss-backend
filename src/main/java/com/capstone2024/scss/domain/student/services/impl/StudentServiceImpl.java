package com.capstone2024.scss.domain.student.services.impl;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.student.dto.*;
import com.capstone2024.scss.domain.common.entity.Semester;
import com.capstone2024.scss.domain.common.mapper.account.AcademicDepartmentDetailMapper;
import com.capstone2024.scss.domain.common.mapper.account.ProfileMapper;
import com.capstone2024.scss.domain.demand.entities.DemandProblemTag;
import com.capstone2024.scss.domain.demand.entities.ProblemTag;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.entities.StudentCounselingProfile;
import com.capstone2024.scss.domain.student.enums.CounselingProfileStatus;
import com.capstone2024.scss.domain.student.services.StudentService;
import com.capstone2024.scss.domain.common.mapper.student.StudentMapper;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.infrastructure.data.fap.dto.DemandProblemTagFapResponseDTO;
import com.capstone2024.scss.infrastructure.repositories.SemesterRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.DemandProblemTagRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.ProblemTagRepository;
import com.capstone2024.scss.infrastructure.repositories.student.CounselingProfileRepository;
import com.capstone2024.scss.infrastructure.repositories.student.StudentRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingAppointmentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
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

        String url = fapServerUrl + "/api/studies/" + student.getStudentCode();
        StudyDTO[] studiesArray = restTemplate.getForObject(url, StudyDTO[].class);
        if(studiesArray == null) {
            return new ArrayList<>();
        }
        return List.of(studiesArray);
    }

    @Override
    public List<AttendanceDTO> getAttendanceByStudentCodeAndSemesterName(Long studentId, String semesterName) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + studentId));

        Semester semester = semesterRepository.findByName(semesterName)
                .orElseThrow(() -> new NotFoundException("Semester not found with name: " + semesterName));

        String url = fapServerUrl + "/api/students/" + student.getStudentCode() + "/semester/" + semesterName;
        AttendanceDTO[] attendanceDTOS = restTemplate.getForObject(url, AttendanceDTO[].class);
        if(attendanceDTOS == null) {
            return new ArrayList<>();
        }
        return List.of(attendanceDTOS);
    }

    @Override
    public List<AttendanceDetailDTO> getAttendanceDetailsByStudentCodeAndAttendanceId(Long studentId, Long attendanceId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + studentId));

        String url = fapServerUrl + "/api/students/" + student.getStudentCode() + "/attendance/" + attendanceId;
        AttendanceDetailDTO[] attendanceDTOS = restTemplate.getForObject(url, AttendanceDetailDTO[].class);
        if(attendanceDTOS == null) {
            return new ArrayList<>();
        }
        return List.of(attendanceDTOS);
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
                                    map.forEach((key, list) -> result.put(key, mergeByProblemTagName(list)));
                                    return result;
                                }
                        )
                ));

        return res;

    }

    @Override
    public PaginationDTO<List<StudentDetailForFilterDTO>> getStudentsWithRecommend(StudentFilterRequestDTO filterRequest) {
        List<String> tagList = parseTagList(filterRequest.getBehaviorOption().getPrompt());
        Page<Student> studentPage = studentRepository.findStudentsByProblemTagsRecommend(
                filterRequest.getAcademicOption().getSpecializationId(),
                filterRequest.getKeyword(),
                filterRequest.getAcademicOption().getDepartmentId(),
                filterRequest.getAcademicOption().getMajorId(),
                tagList,
                filterRequest.getBehaviorOption().getSemesterId(), filterRequest.getPagination());
        List<StudentDetailForFilterDTO> studentDTOs = studentPage.getContent().stream()
                .map(student -> toStudentDetailDTO(student, tagList, filterRequest))
                .collect(Collectors.toList());

        return PaginationDTO.<List<StudentDetailForFilterDTO>>builder()
                .data(studentDTOs)
                .totalPages(studentPage.getTotalPages())
                .totalElements((int) studentPage.getTotalElements())
                .build();
    }

    @Override
    public void excludeAllDemandProblemTagsByStudentId(Long studentId) {
        List<DemandProblemTag> demandProblemTags = demandProblemTagRepository.findByStudentIdAndIsExcludedFalse(studentId);

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
        Page<Student> studentPage = null;
        List<StudentDetailForFilterDTO> studentDTOs = null;
        if(filterRequest.isIncludeBehavior()) {
            List<String> tagList = parseTagList(filterRequest.getBehaviorOption() != null ? filterRequest.getBehaviorOption().getPrompt() : null);
            studentPage = studentRepository.findStudentsByProblemTagsAndOptionalSemester(
                    filterRequest.getAcademicOption().getSpecializationId(),
                    filterRequest.getKeyword(),
                    filterRequest.getAcademicOption().getDepartmentId(),
                    filterRequest.getAcademicOption().getMajorId(),
                    tagList,
                    filterRequest.getBehaviorOption().getSemesterId(), filterRequest.getPagination());
            studentDTOs = studentPage.getContent().stream()
                    .map(student -> toStudentDetailDTO(student, tagList, filterRequest))
                    .collect(Collectors.toList());
        } else {
            studentPage = studentRepository.findStudents(
                    filterRequest.getAcademicOption().getSpecializationId(),
                    filterRequest.getKeyword(),
                    filterRequest.getAcademicOption().getDepartmentId(),
                    filterRequest.getAcademicOption().getMajorId(),
                    filterRequest.getPagination());
            studentDTOs = studentPage.getContent().stream()
                    .map(student -> toStudentDetailDTO(student, null, filterRequest))
                    .collect(Collectors.toList());
        }
        return PaginationDTO.<List<StudentDetailForFilterDTO>>builder()
                .data(studentDTOs)
                .totalPages(studentPage.getTotalPages())
                .totalElements((int) studentPage.getTotalElements())
                .build();
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

    @Value("${openai.api.key}")
    private String openAiApiKey;

    public String callOpenAPI(String prompt) {

        // Tạo đối tượng Content
        OpenAIRequest.Message.Content content = new OpenAIRequest.Message.Content("text",prompt);
// Tạo đối tượng Message
        OpenAIRequest.Message message = new OpenAIRequest.Message("user", List.of(content));

// Tạo đối tượng ResponseFormat
        OpenAIRequest.ResponseFormat.JsonSchema.Schema.Properties.Result.Items resultItems = new OpenAIRequest.ResponseFormat.JsonSchema.Schema.Properties.Result.Items("string", "A string tag in the array.");
        OpenAIRequest.ResponseFormat.JsonSchema.Schema.Properties.Result result = new OpenAIRequest.ResponseFormat.JsonSchema.Schema.Properties.Result(
                "array",
                "An array of strings.",
                resultItems
        );

        OpenAIRequest.ResponseFormat.JsonSchema.Schema.Properties properties = new OpenAIRequest.ResponseFormat.JsonSchema.Schema.Properties(result);

        OpenAIRequest.ResponseFormat.JsonSchema.Schema schema = new OpenAIRequest.ResponseFormat.JsonSchema.Schema(
                "object",
                List.of("result"),
                properties,
                false
        );

        OpenAIRequest.ResponseFormat.JsonSchema jsonSchema = new OpenAIRequest.ResponseFormat.JsonSchema("string_array", true, schema);

        OpenAIRequest.ResponseFormat responseFormat = new OpenAIRequest.ResponseFormat("json_schema", jsonSchema);

// Tạo đối tượng OpenAIRequest
        OpenAIRequest request = new OpenAIRequest(
                "gpt-4o",
                List.of(message),
                0.3,
                2048,
                1,
                0,
                0,
                responseFormat
        );

        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + openAiApiKey);

        String openAiUrl = "https://api.openai.com/v1/chat/completions";

        // Tạo đối tượng HttpEntity để gửi yêu cầu
        HttpEntity<OpenAIRequest> entity = new HttpEntity<>(request, headers);

        ObjectMapper objectMapper = new ObjectMapper();
        // Gửi yêu cầu POST tới API của OpenAI
        ResponseEntity<String> response = restTemplate.exchange(openAiUrl, HttpMethod.POST, entity, String.class);
        // Ánh xạ JSON response thành đối tượng OpenAIResponse
        try {
            OpenAIResponse openAIResponse = objectMapper.readValue(response.getBody(), OpenAIResponse.class);

            // Nối các nội dung message content từ choices
            List<String> contents = openAIResponse.getChoices().stream()
                    .map(choice -> choice.getMessage().getContent())
                    .collect(Collectors.toList());

            JsonNode rootNode = objectMapper.readTree(contents.getFirst());

            JsonNode resultNode = rootNode.get("result");

            return String.join(", ",
                    objectMapper.convertValue(resultNode, List.class));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing OpenAI response", e);
        }

    }

    public List<String> parseTagList(String input) {
        String openAICommand = generatePromptToOpenAI(input == null ? "" : input);
        return Arrays.asList(callOpenAPI(openAICommand).split(",\\s*"));
    }

    public StudentDetailForFilterDTO toStudentDetailDTO(Student student, List<String> tagList, StudentFilterRequestDTO filterRequestDTO) {
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

        if(filterRequestDTO.isIncludeBehavior()) {
            List<DemandProblemTag> demandProblemTags = demandProblemTagRepository.findByStudentIdAndSemesterId(student.getId(), filterRequestDTO.getBehaviorOption().getSemesterId());

            List<DemandProblemTagResponseDTO> demandProblemTagResponseDTOS = demandProblemTags.stream().map(demandProblemTag -> {
                boolean isContained = tagList.stream().anyMatch(tag -> tag.equals(demandProblemTag.getProblemTag().getName()));
                return DemandProblemTagResponseDTO.builder()
                        .problemTagName(demandProblemTag.getProblemTag().getName())
                        .isContained(isContained)
                        .build();
            }).collect(Collectors.toList());

            studentDetailForFilterDTO.setBehaviorTagList(mergeByProblemTagName(demandProblemTagResponseDTOS));
        } else {
            studentDetailForFilterDTO.setBehaviorTagList(new ArrayList<>());
        }

        return studentDetailForFilterDTO;
    }

    private List<DemandProblemTagResponseDTO> mergeByProblemTagName(List<DemandProblemTagResponseDTO> inputList) {
        Map<String, DemandProblemTagResponseDTO> mergedMap = new HashMap<>();

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

        // Chuyển kết quả từ map sang list
        return new ArrayList<>(mergedMap.values());
    }
}

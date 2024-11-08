package com.capstone2024.scss.application.student;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.counseling_appointment.dto.request.counseling_appointment.AppointmentFilterDTO;
import com.capstone2024.scss.application.student.dto.*;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentService;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "API endpoints for managing students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final CounselingAppointmentService appointmentService;
    private final RestTemplate restTemplate;

    @Value("${server.api.fap.system.base.url}")
    private String fapServerUrl;

    @PostMapping("/document/info")
    public ResponseEntity<Object> createCounselingProfile(
            @RequestBody StudentCounselingProfileRequestDTO requestDTO,
            @NotNull @AuthenticationPrincipal Account principle) {
        Long studentId = principle.getProfile().getId();
        studentService.createCounselingProfile(requestDTO, studentId);
        return ResponseUtil.getResponse("Create Successfull", HttpStatus.OK);
    }

    @PutMapping("/document/info")
    public ResponseEntity<Object> updateCounselingProfile(
            @RequestBody StudentCounselingProfileRequestDTO requestDTO,
            @NotNull @AuthenticationPrincipal Account principle) {
        Long studentId = principle.getProfile().getId();
        studentService.updateCounselingProfile(requestDTO, studentId);
        return ResponseUtil.getResponse("Update Successful", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getStudentById(@PathVariable Long id) {
        StudentProfileDTO studentProfileDTO = studentService.getStudentById(id);
        return ResponseUtil.getResponse(studentProfileDTO, HttpStatus.OK);
    }

    @GetMapping("/code/{studentCode}")
    public ResponseEntity<Object> getStudentByStudentCode(@PathVariable String studentCode) {
        StudentProfileDTO studentProfileDTO = studentService.getStudentByStudentCode(studentCode);
        return ResponseUtil.getResponse(studentProfileDTO, HttpStatus.OK);
    }

    @GetMapping("/document/{id}")
    public ResponseEntity<Object> getStudentDocumentById(@PathVariable Long id) {
        StudentDocumentDTO studentProfileDTO = studentService.getStudentDocumentById(id);
        return ResponseUtil.getResponse(studentProfileDTO, HttpStatus.OK);
    }

    @GetMapping("/document/info")
    public ResponseEntity<Object> getStudentDocument(
                                                     @NotNull @AuthenticationPrincipal Account principle
    ) {
        StudentDocumentDTO studentProfileDTO = studentService.getStudentDocumentById(principle.getProfile().getId());
        return ResponseUtil.getResponse(studentProfileDTO, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<Object> getStudents(
            @RequestParam(name = "keyword", required = false) String keyword,

            @RequestParam(name = "specializationId", required = false) Long specializationId,
            @RequestParam(name = "departmentId", required = false) Long departmentId,
            @RequestParam(name = "majorId", required = false) Long majorId,
            @RequestParam(name = "currentTerm", required = false) Integer currentTerm,

            @RequestParam(name = "semesterIdForGPA", required = false) Long semesterIdForGPA,
            @RequestParam(name = "minGPA", required = false) BigDecimal minGPA,
            @RequestParam(name = "maxGPA", required = false) BigDecimal maxGPA,

            @RequestParam(name = "isIncludeBehavior", required = true, defaultValue = "false") boolean isIncludeBehavior,
            @RequestParam(name = "semesterIdForBehavior", required = false) Long semesterIdForBehavior,
            @RequestParam(name = "promptForBehavior", required = false) String promptForBehavior,

            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        if (page < 1) {
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        StudentFilterRequestDTO filterRequest = StudentFilterRequestDTO.builder()
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .keyword(keyword != null && !keyword.isEmpty() ? keyword.trim() : null)
                .pagination(PageRequest.of(page - 1, 10, Sort.by(
                        sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .academicOption(StudentAcademicFilterDTO.builder()
                        .specializationId(specializationId)
                        .departmentId(departmentId)
                        .majorId(majorId)
                        .currentTerm(currentTerm)
                        .build())
                .gpaOption(StudentGPAFilterDTO.builder()
                        .semesterId(semesterIdForGPA)
                        .min(minGPA)
                        .max(maxGPA)
                        .build())
                .isIncludeBehavior(isIncludeBehavior)
                .behaviorOption(StudentBehaviorFilterDTO.builder()
                        .semesterId(semesterIdForBehavior)
                        .prompt(promptForBehavior != null ? promptForBehavior : "Creative Thinking, Good Time Management Skills")
                        .build())
                .build();

        PaginationDTO<List<StudentDetailForFilterDTO>> responseDTO = studentService.getStudents(filterRequest);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/appointment/filter/{studentId}")
    public ResponseEntity<Object> getAppointmentsForStudent(
            @PathVariable Long studentId,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "SortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        AppointmentFilterDTO filterDTO = AppointmentFilterDTO.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .status(CounselingAppointmentStatus.ATTEND)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .build();

        PaginationDTO<List<CounselingAppointmentDTO>> responseDTO = appointmentService.getAppointmentsWithFilterForStudent(filterDTO, studentId);

        return ResponseUtil.getResponse(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/study/{studentId}")
    public ResponseEntity<Object> getStudiesByStudentCode(@PathVariable Long studentId) {
        List<StudyDTO> studies = studentService.getStudiesByStudentId(studentId);
        return ResponseUtil.getResponse(studies, HttpStatus.OK);
    }

    @GetMapping("/{studentId}/semester/{semesterName}")
    public ResponseEntity<Object> getAttendanceByStudentCodeAndSemesterName(
            @PathVariable Long studentId,
            @PathVariable String semesterName) {

        List<AttendanceDTO> attendances = studentService.getAttendanceByStudentCodeAndSemesterName(studentId, semesterName);
        return ResponseUtil.getResponse(attendances, HttpStatus.OK);
    }

    @GetMapping("/{studentId}/attendance/{attendanceId}")
    public ResponseEntity<Object> getAttendanceDetailsByStudentCodeAndAttendanceId(
            @PathVariable Long studentId,
            @PathVariable Long attendanceId) {

        List<AttendanceDetailDTO> details = studentService.getAttendanceDetailsByStudentCodeAndAttendanceId(studentId, attendanceId);
        return ResponseUtil.getResponse(details, HttpStatus.OK);
    }

    @GetMapping("/{studentId}/problem-tag/detail/semester/{semesterName}")
    public ResponseEntity<Object> getDemandProblemTagDetail(
            @PathVariable Long studentId,
            @PathVariable String semesterName) {


        return ResponseUtil.getResponse(studentService.getDemandProblemTagDetailByStudentAndSemester(studentId, semesterName), HttpStatus.OK);
    }

    @GetMapping("/recommendation/filter")
    public ResponseEntity<Object> getStudentsWithRecommendation(
            @RequestParam(name = "keyword", required = false) String keyword,

            @RequestParam(name = "specializationId", required = false) Long specializationId,
            @RequestParam(name = "departmentId", required = false) Long departmentId,
            @RequestParam(name = "majorId", required = false) Long majorId,

            @RequestParam(name = "semesterIdForBehavior", required = false) Long semesterIdForBehavior,
            @RequestParam(name = "promptForBehavior", required = false) String promptForBehavior,

            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        if (page < 1) {
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        StudentFilterRequestDTO filterRequest = StudentFilterRequestDTO.builder()
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .keyword(keyword != null && !keyword.isEmpty() ? keyword.trim() : null)
                .pagination(PageRequest.of(page - 1, 10, Sort.by(
                        sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .academicOption(StudentAcademicFilterDTO.builder()
                        .specializationId(specializationId)
                        .departmentId(departmentId)
                        .majorId(majorId)
                        .build())
                .isIncludeBehavior(true)
                .behaviorOption(StudentBehaviorFilterDTO.builder()
                        .semesterId(semesterIdForBehavior)
                        .prompt(promptForBehavior)
                        .build())
                .build();

        PaginationDTO<List<StudentDetailForFilterDTO>> responseDTO = studentService.getStudentsWithRecommend(filterRequest);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/problem-tag/exclude-all/{studentId}")
    public ResponseEntity<String> excludeAllTagsForStudent(@PathVariable Long studentId) {
        studentService.excludeAllDemandProblemTagsByStudentId(studentId);
        return ResponseEntity.ok("All DemandProblemTags for student " + studentId + " have been excluded.");
    }
}


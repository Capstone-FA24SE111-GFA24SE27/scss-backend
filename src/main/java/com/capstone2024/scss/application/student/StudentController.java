package com.capstone2024.scss.application.student;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.booking_counseling.dto.CounselingAppointmentDTO;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.counseling_appointment.dto.request.counseling_appointment.AppointmentFilterDTO;
import com.capstone2024.scss.application.student.dto.StudentCounselingProfileRequestDTO;
import com.capstone2024.scss.application.student.dto.StudentDocumentDTO;
import com.capstone2024.scss.application.student.dto.StudentFilterRequestDTO;
import com.capstone2024.scss.application.student.dto.StudyDTO;
import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counseling_booking.services.CounselingAppointmentService;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final CounselingAppointmentService appointmentService;

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
            @RequestParam(name = "studentCode", required = false) String studentCode,
            @RequestParam(name = "specializationId", required = false) Long specializationId,
            @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") SortDirection sortDirection,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        if (page < 1) {
            throw new IllegalArgumentException("Page must be positive (page > 0)");
        }

        StudentFilterRequestDTO filterRequest = StudentFilterRequestDTO.builder()
                .studentCode(studentCode != null && !studentCode.isEmpty() ? studentCode.trim() : null)
                .specializationId(specializationId)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .keyword(keyword != null && !keyword.isEmpty() ? keyword.trim() : null)
                .pagination(PageRequest.of(page - 1, 10, Sort.by(
                        sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)))
                .build();

        PaginationDTO<List<StudentProfileDTO>> responseDTO = studentService.getStudents(filterRequest);
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
}


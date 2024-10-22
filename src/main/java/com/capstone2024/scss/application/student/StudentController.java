package com.capstone2024.scss.application.student;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.student.dto.StudentDocumentDTO;
import com.capstone2024.scss.application.student.dto.StudentFilterRequestDTO;
import com.capstone2024.scss.domain.student.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

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
}


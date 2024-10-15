package com.capstone2024.scss.application.student;

import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import com.capstone2024.scss.application.student.dto.StudentDocumentDTO;
import com.capstone2024.scss.domain.student.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/document/{id}")
    public ResponseEntity<Object> getStudentDocumentById(@PathVariable Long id) {
        StudentDocumentDTO studentProfileDTO = studentService.getStudentDocumentById(id);
        return ResponseUtil.getResponse(studentProfileDTO, HttpStatus.OK);
    }
}


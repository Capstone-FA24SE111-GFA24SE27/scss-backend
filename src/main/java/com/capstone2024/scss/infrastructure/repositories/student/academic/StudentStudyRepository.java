package com.capstone2024.scss.infrastructure.repositories.student.academic;

import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.entities.academic.StudentStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentStudyRepository extends JpaRepository<StudentStudy, Long> {
    List<StudentStudy> findByStudent_StudentCodeAndSemester_Name(String studentCode, String semesterName);
    List<StudentStudy> findByStudent(Student student);
}

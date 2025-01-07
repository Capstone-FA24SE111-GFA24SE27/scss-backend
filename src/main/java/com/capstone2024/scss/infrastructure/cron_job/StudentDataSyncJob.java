package com.capstone2024.scss.infrastructure.cron_job;

import com.capstone2024.scss.domain.counselor.entities.Specialization;
import com.capstone2024.scss.domain.student.entities.Department;
import com.capstone2024.scss.domain.student.entities.Major;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.entities.academic.StudentStudy;
import com.capstone2024.scss.infrastructure.configuration.application.StudentSynchronizeService;
import com.capstone2024.scss.infrastructure.data.fap.dto.StudentFapResponseDTO;
import com.capstone2024.scss.infrastructure.repositories.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StudentDataSyncJob {
    private final StudentSynchronizeService studentSynchronizeService;
    private final RestTemplate restTemplate;
    @Value("${server.api.fap.system.base.url}")
    private String fapServerUrl;

    /**
     * Đồng bộ dữ liệu sinh viên lúc 12 giờ tối.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void syncStudentDataAtMidnight() {
        studentSynchronizeService.syncAcademicField();
    }

    /**
     * Đồng bộ dữ liệu sinh viên lúc 1 giờ sáng.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void syncStudentDataAt1AM() {
        ResponseEntity<StudentFapResponseDTO[]> response = restTemplate.getForEntity(fapServerUrl + "/api/students", StudentFapResponseDTO[].class);
        if (response.getBody() != null) {
            List<StudentFapResponseDTO> studentDTOs = List.of(response.getBody());
            for (StudentFapResponseDTO dto : studentDTOs) {
                studentSynchronizeService.syncOneStudent(dto.getStudentCode());
            }
        }
    }

    /**
     * Đồng bộ dữ liệu sinh viên lúc 5 giờ sáng.
     */
    @Scheduled(cron = "0 0 5 * * ?")
    public void syncStudentDataAt5AM() {
        studentSynchronizeService.getYesterdayDetailsWithCommentsAndSaveTag();
    }
}


package com.capstone2024.scss.infrastructure.cron_job;

import com.capstone2024.scss.domain.common.entity.Semester;
import com.capstone2024.scss.infrastructure.repositories.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class SemesterScheduler {

    private final SemesterRepository semesterRepository;
    private final RestTemplate restTemplate;

    @Value("${server.api.fap.system.base.url}")
    private String fapServerUrl;

    @Scheduled(cron = "0 0 0 1 1,5,9 ?") // Chạy vào đầu tháng 1, 5, 9
    @Transactional
    public void fetchAndAddNewSemester() {
        try {
            // Gọi server ngoài để lấy tên học kỳ
            String semesterName = restTemplate.getForObject(fapServerUrl + "/api/semester/new", String.class);

            if (semesterName != null && !semesterName.isEmpty()) {
                // Tạo đối tượng Semester mới
                Semester newSemester = Semester.builder()
                        .name(semesterName)
                        .softDelete(false) // Trạng thái mặc định
                        .build();

                // Lưu vào cơ sở dữ liệu
                semesterRepository.save(newSemester);
                System.out.println("New semester added: " + semesterName);
            } else {
                System.err.println("Failed to fetch semester name from API.");
            }
        } catch (Exception e) {
            System.err.println("Error occurred while fetching or saving semester: " + e.getMessage());
        }
    }
}


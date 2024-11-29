package com.capstone2024.scss.infrastructure.cron_job;

import com.capstone2024.scss.infrastructure.repositories.demand.DemandProblemTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DemandProblemTagScheduler {

    private final DemandProblemTagRepository demandProblemTagRepository;

    @Scheduled(cron = "0 0 0 1 1,5,9 ?") // Chạy vào đầu tháng 1, 5, 9
    @Transactional
    public void updateIsExcludedForAllTags() {
        try {
            // Cập nhật tất cả DemandProblemTag.isExcluded thành true
            int updatedCount = demandProblemTagRepository.updateIsExcludedForAllTags(true);

            // Log kết quả
            log.info("Updated " + updatedCount + " DemandProblemTag entries to set isExcluded=true.");
        } catch (Exception e) {
            log.error("Error occurred while updating DemandProblemTag entries: " + e.getMessage());
        }
    }
}


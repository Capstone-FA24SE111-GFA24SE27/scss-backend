package com.capstone2024.scss.infrastructure.cron_job;

import com.capstone2024.scss.domain.q_and_a.entities.QuestionCard;
import com.capstone2024.scss.domain.q_and_a.service.QuestionCardService;
import com.capstone2024.scss.infrastructure.repositories._and_a.QuestionCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class QuestionCardCronJob {

    private final QuestionCardRepository questionCardRepository;
    private final QuestionCardService questionCardService;

    @Scheduled(cron = "0 0 12 * * ?") // Chạy vào 12 giờ hằng ngày
    @Transactional
    public void closeOldQuestionCards() {
        LocalDateTime twoDaysAgo = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).minus(2, ChronoUnit.DAYS);

        // Lấy tất cả QuestionCard thỏa mãn điều kiện
        questionCardRepository.findAllByIsClosedFalseAndCreatedDateBeforeAndAnswerIsNotNull(twoDaysAgo)
                .forEach(questionCard -> {
                    questionCardService.closeQuestionCardForCounselor(questionCard.getId(), questionCard.getCounselor().getId());
                });
    }
}

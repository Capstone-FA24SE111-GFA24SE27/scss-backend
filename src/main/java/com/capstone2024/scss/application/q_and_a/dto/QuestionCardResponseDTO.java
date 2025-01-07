package com.capstone2024.scss.application.q_and_a.dto;

import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionCard;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionCardStatus;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionCardResponseDTO {
    private Long id;
    private String answer;
    private String content;
    private String title;
    private QuestionType questionType;
    private boolean isTaken;
    private boolean isClosed;
    private boolean isAccepted;
    private QuestionCardStatus status;
    private StudentProfileDTO student;
    private CounselorProfileDTO counselor;
    private ChatSessionDTO chatSession;
    private String reviewReason;
    private TopicDTO topic;
    private LocalDateTime createdDate;
    private QuestionCard.QuestionCarDifficultyLevel difficultyLevel;
    private QuestionCard.PublicStatus publicStatus;
    private QuestionCardFeedbackDTO feedback;
}

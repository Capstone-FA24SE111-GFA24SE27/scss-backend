package com.capstone2024.scss.domain.common.mapper.q_and_a;

import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import com.capstone2024.scss.application.account.dto.StudentProfileDTO;
import com.capstone2024.scss.application.q_and_a.dto.QuestionCardResponseDTO;
import com.capstone2024.scss.domain.common.mapper.account.CounselorProfileMapper;
import com.capstone2024.scss.domain.common.mapper.student.StudentMapper;
import com.capstone2024.scss.domain.counselor.entities.AcademicCounselor;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.counselor.entities.NonAcademicCounselor;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionCard;

public class QuestionCardMapper {

    /**
     * Chuyển đổi QuestionCard entity sang QuestionCardResponseDto.
     *
     * @param questionCard QuestionCard entity
     * @return QuestionCardResponseDto
     */
    public static QuestionCardResponseDTO toQuestionCardResponseDto(QuestionCard questionCard) {
        if (questionCard == null) {
            return null;
        }

        StudentProfileDTO studentDTO = StudentMapper.toStudentProfileDTO(questionCard.getStudent());

        CounselorProfileDTO counselorDTO = null;
        if (questionCard.getCounselor() != null) {
            Counselor counselor = questionCard.getCounselor();
            if(counselor instanceof AcademicCounselor) {
                counselorDTO = CounselorProfileMapper.toAcademicCounselorProfileDTO((AcademicCounselor) counselor);
            } else if (counselor instanceof NonAcademicCounselor) {
                counselorDTO = CounselorProfileMapper.toNonAcademicCounselorProfileDTO((NonAcademicCounselor) counselor);
            }
        }

        return QuestionCardResponseDTO.builder()
                .id(questionCard.getId())
                .answer(questionCard.getAnswer())
                .content(questionCard.getContent())
                .questionType(questionCard.getQuestionType())
                .title(questionCard.getTitle())
//                .isTaken(questionCard.isTaken())
                .isClosed(questionCard.isClosed())
                .isAccepted(questionCard.isAccepted())
                .status(questionCard.getStatus())
                .student(studentDTO)
                .counselor(counselorDTO)
                .publicStatus(questionCard.getPublicStatus())
                .reviewReason(questionCard.getReviewReason())
                .chatSession(ChatSessionMapper.toChatSessionDTO(questionCard.getChatSession()))
                .createdDate(questionCard.getCreatedDate())
                .difficultyLevel(questionCard.getDifficultyLevel())
                .feedback(QuestionCardFeedbackMapper.toQuestionCardDTO(questionCard.getFeedback()))
//                .topic(TopicMapper.toDTO(questionCard.getTopic()))
                .build();
    }
}

package com.capstone2024.scss.application.contribution_question_card.dto;

import com.capstone2024.scss.application.account.dto.CounselorProfileDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ContributionQuestionCardResponseDTO {

    private String id;            // Sử dụng String để hỗ trợ cả MySQL và ElasticSearch ID
    private String question;
    private String title;
    private String answer;
    private String status;        // Trả về dưới dạng String cho sự đơn giản
    private CounselorProfileDTO counselor;
    private ContributedQuestionCardCategoryDTO category;
}


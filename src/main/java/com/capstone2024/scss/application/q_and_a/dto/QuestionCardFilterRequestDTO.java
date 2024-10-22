package com.capstone2024.scss.application.q_and_a.dto;

import com.capstone2024.scss.application.account.dto.enums.SortDirection;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionCardStatus;
import com.capstone2024.scss.domain.q_and_a.enums.QuestionType;
import lombok.*;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCardFilterRequestDTO {
    private String keyword; // Từ khóa tìm kiếm trong nội dung hoặc tiêu đề
    private QuestionCardStatus status; // Trạng thái của thẻ câu hỏi
    private Boolean isTaken; // Đã được nhận bởi counselor chưa
    private Boolean isClosed; // Thẻ câu hỏi đã bị đóng chưa
    private Boolean isChatSessionClosed; // Session chat có bị đóng chưa
    private String sortBy; // Trường để sắp xếp
    private SortDirection sortDirection; // Hướng sắp xếp (ASC hoặc DESC)
    private Pageable pagination;
    private QuestionType type;
    private String studentCode;
    private Long topicId;
}

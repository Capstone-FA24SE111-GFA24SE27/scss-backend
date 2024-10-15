package com.capstone2024.scss.domain.q_and_a.enums;

public enum QuestionCardStatus {
    PENDING,    // Trạng thái mặc định khi sinh viên tạo thẻ câu hỏi
    VERIFIED,   // Thẻ câu hỏi đã được kiểm duyệt
    FLAGGED,    // Thẻ câu hỏi bị gắn cờ do vi phạm hoặc cần kiểm tra thêm
    REJECTED    // Thẻ câu hỏi bị từ chối
}

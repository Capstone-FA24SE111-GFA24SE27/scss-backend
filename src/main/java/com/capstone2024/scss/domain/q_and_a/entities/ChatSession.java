package com.capstone2024.scss.domain.q_and_a.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.student.entities.Student;
import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_session")
public class ChatSession extends BaseEntity {

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate; // Ngày bắt đầu session

    @Column(name = "last_interaction_date", nullable = false)
    private LocalDateTime lastInteractionDate; // Ngày tương tác cuối cùng

    @Column(name = "is_closed", nullable = false)
    private boolean isClosed = false; // Đóng session sau 2 ngày không tương tác

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_card_id", nullable = false)
    private QuestionCard questionCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // Sinh viên tham gia session

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counselor_id", nullable = false)
    private Counselor counselor; // Tư vấn viên tham gia session

    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>(); // Danh sách tin nhắn trong session

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        this.startDate = LocalDateTime.now();
        this.lastInteractionDate = LocalDateTime.now();
    }
}

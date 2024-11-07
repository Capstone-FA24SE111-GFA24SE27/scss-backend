package com.capstone2024.scss.domain.q_and_a.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.q_and_a.enums.TopicType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "topic")
public class Topic extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name; // Tên chủ đề

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TopicType type; // Loại chủ đề (ACADEMIC hoặc NON_ACADEMIC)

//    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<QuestionCard> questionCards; // Các câu hỏi thuộc chủ đề này
}

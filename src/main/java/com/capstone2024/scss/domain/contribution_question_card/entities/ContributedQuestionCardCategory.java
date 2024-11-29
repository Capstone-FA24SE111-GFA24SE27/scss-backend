package com.capstone2024.scss.domain.contribution_question_card.entities;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "contributed_question_card_category")
public class ContributedQuestionCardCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    public enum Type {
        ACADEMIC,
        NON_ACADEMIC
    }
}

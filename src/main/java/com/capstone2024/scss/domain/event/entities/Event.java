package com.capstone2024.scss.domain.event.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event")
public class Event extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, name = "display_image")
    private String displayImage;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private int view = 0;

    @Column(name = "is_need_accept")
    private Boolean isNeedAccept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false) // New column to reference Semester
    private Semester semester;
}

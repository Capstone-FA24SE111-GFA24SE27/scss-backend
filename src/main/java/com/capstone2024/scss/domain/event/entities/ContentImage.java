package com.capstone2024.scss.domain.event.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content_image")
public class ContentImage extends BaseEntity {

    @Column(nullable = false)
    private String imageUrl; // URL của hình ảnh

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false) // Many-to-One với Event
    private Event event;
}

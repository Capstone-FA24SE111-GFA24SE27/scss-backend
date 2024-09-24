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
@Table(name = "recap_video")
public class RecapVideo extends BaseEntity {

    @Column(nullable = false)
    private String videoUrl; // URL của video tóm tắt

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false) // Many-to-One với Event
    private Event event;
}

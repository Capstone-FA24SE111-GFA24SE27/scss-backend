package com.capstone2024.scss.domain.counselor.entities;

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
@Table(name = "qualifications")
public class Qualification extends BaseEntity {

    @Column(name = "degree", nullable = false)
    private String degree;

    @Column(name = "field_of_study", nullable = false)
    private String fieldOfStudy;

    @Column(name = "institution", nullable = false)
    private String institution;

    @Column(name = "year_of_graduation")
    private Integer yearOfGraduation;

    @Column(name = "image_url") // Trường ảnh
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counselor_id", nullable = false)
    private Counselor counselor;
}


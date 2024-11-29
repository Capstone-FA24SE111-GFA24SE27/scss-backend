package com.capstone2024.scss.domain.counselor.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "certifications")
public class Certification extends BaseEntity{

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "organization", nullable = false)
    private String organization;

    @Column(name = "image_url") // Trường ảnh
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counselor_id", nullable = false)
    private Counselor counselor;
}


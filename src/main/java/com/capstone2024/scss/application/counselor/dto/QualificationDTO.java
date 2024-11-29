package com.capstone2024.scss.application.counselor.dto;

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
public class QualificationDTO {
    private Long id;               // ID của qualification, kế thừa từ BaseEntity
    private String degree;         // Bằng cấp
    private String fieldOfStudy;   // Ngành học
    private String institution;    // Trường học
    private Integer yearOfGraduation; // Năm tốt nghiệp
    private String imageUrl;       // Đường dẫn đến ảnh
}

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
public class CertificationDTO {
    private Long id;               // ID của certification, kế thừa từ BaseEntity
    private String name;           // Tên chứng chỉ
    private String organization;   // Tổ chức cấp chứng chỉ
    private String imageUrl;       // Đường dẫn đến ảnh
}


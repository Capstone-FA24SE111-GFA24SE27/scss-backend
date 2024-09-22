package com.capstone2024.scss.application.event.dto;

import com.capstone2024.scss.application.event.category.dto.CategoryDTO;
import com.capstone2024.scss.application.event.semester.dto.SemesterDTO;
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
public class EventDTO {
    private Long id;
    private String title;
    private String address;
    private String content;
    private int view;
    private Boolean isNeedAccept;
    private String displayImage; // Thêm trường cho hình ảnh hiển thị
    private SemesterDTO semester; // Thêm SemesterDTO
    private CategoryDTO category; // Thêm CategoryDTO
}

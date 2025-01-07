package com.capstone2024.scss.infrastructure.elastic_search.documents;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "question_cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionCardDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long sortingId;

    @Field(type = FieldType.Text)
    private String title; // Nội dung câu hỏi

    @Field(type = FieldType.Text)
    private String content; // Nội dung câu hỏi

    @Field(type = FieldType.Text)
    private String answer; // Tiêu đề câu hỏi

    @Field(type = FieldType.Text)
    private String reviewReason; // Nội dung lý do xem xét

    @Field(type = FieldType.Keyword)
    private String questionType; // Loại câu hỏi (ACADEMIC, NON_ACADEMIC)

    @Field(type = FieldType.Boolean)
    private boolean isClosed; // Trạng thái đóng

    @Field(type = FieldType.Date)
    @JsonIgnore
    private LocalDateTime closedDate; // Thời gian đóng

    @Field(type = FieldType.Keyword)
    private String status; // Trạng thái câu hỏi (PENDING, VERIFIED, FLAGGED, REJECTED)

    @Field(type = FieldType.Keyword)
    private String difficultyLevel; // Mức độ khó (Easy, Medium, Hard)

    @Field(type = FieldType.Keyword)
    private String studentId; // ID của sinh viên

    @Field(type = FieldType.Keyword)
    private String counselorId; // ID của người tư vấn (nếu có)

    @Field(type = FieldType.Dense_Vector, dims = 1536) // Đảm bảo số chiều phù hợp với OpenAI embeddings
    private List<Float> titleVector; // Vector cho tiêu đề

    @Field(type = FieldType.Dense_Vector, dims = 1536) // Đảm bảo số chiều phù hợp với OpenAI embeddings
    private List<Float> contentVector; // Vector cho nội dung câu hỏi
}


package com.capstone2024.scss.infrastructure.elastic_search.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "contribution_question_cards")
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContributionQuestionCardDocument {

    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Long)
    private Long sortingId;

    @Field(type = FieldType.Text)
    private String question;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String answer;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private String counselorId;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Dense_Vector, dims = 1536) // Đảm bảo số chiều phù hợp với OpenAI embeddings
    private List<Float> questionVector;

    @Field(type = FieldType.Dense_Vector, dims = 1536)
    private List<Float> titleVector;

}

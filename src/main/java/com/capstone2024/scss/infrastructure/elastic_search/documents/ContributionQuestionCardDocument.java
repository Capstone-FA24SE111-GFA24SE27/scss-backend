package com.capstone2024.scss.infrastructure.elastic_search.documents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "contribution_question_cards")
@Getter
@Setter
public class ContributionQuestionCardDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String question;

    @Field(type = FieldType.Text)
    private String answer;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private String counselorId;

    @Field(type = FieldType.Keyword)
    private String category;

}

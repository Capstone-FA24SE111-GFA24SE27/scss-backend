package com.capstone2024.scss.infrastructure.elastic_search.repository;

import com.capstone2024.scss.infrastructure.elastic_search.documents.QuestionCardDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

public interface QuestionCardDocumentRepository extends ElasticsearchRepository<QuestionCardDocument, String> {
}

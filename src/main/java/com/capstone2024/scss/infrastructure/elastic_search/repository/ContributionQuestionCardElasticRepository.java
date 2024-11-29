package com.capstone2024.scss.infrastructure.elastic_search.repository;

import com.capstone2024.scss.infrastructure.elastic_search.documents.ContributionQuestionCardDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ContributionQuestionCardElasticRepository extends ElasticsearchRepository<ContributionQuestionCardDocument, String> {
}

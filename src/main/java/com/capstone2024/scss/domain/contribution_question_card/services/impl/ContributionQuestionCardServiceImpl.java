package com.capstone2024.scss.domain.contribution_question_card.services.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.contribution_question_card.dto.ContributionQuestionCardResponseDTO;
import com.capstone2024.scss.domain.common.mapper.contribution_question_card.ContributionQuestionCardMapper;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributedQuestionCardCategory;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributionQuestionCard;
import com.capstone2024.scss.domain.contribution_question_card.services.ContributionQuestionCardService;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.infrastructure.elastic_search.documents.ContributionQuestionCardDocument;
import com.capstone2024.scss.infrastructure.elastic_search.repository.ContributionQuestionCardElasticRepository;
import com.capstone2024.scss.infrastructure.repositories.contribution_question_card.ContributedQuestionCardCategoryRepository;
import com.capstone2024.scss.infrastructure.repositories.contribution_question_card.ContributionQuestionCardRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.CounselorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContributionQuestionCardServiceImpl implements ContributionQuestionCardService {

    private final ContributionQuestionCardRepository questionCardRepository;
    private final ContributionQuestionCardElasticRepository elasticRepository;
    private final ContributedQuestionCardCategoryRepository categoryRepository;
    private final CounselorRepository counselorRepository;

    private Counselor getCounselor(Long counselorId) {

        return counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found"));
    }


    @Override
    @Transactional
    public ContributionQuestionCard createContributionQuestionCard(String question, String answer, Long categoryId, Long counselorId) {
        Counselor counselor = getCounselor(counselorId);

        ContributedQuestionCardCategory category = categoryRepository.findById(categoryId).orElseThrow();
        // Save in MySQL
        ContributionQuestionCard card = new ContributionQuestionCard();
        card.setQuestion(question);
        card.setAnswer(answer);
        card.setStatus(ContributionQuestionCard.Status.UNVERIFIED);  // Default status
        card.setCounselor(counselor);
        card.setCategory(category);

        ContributionQuestionCard savedCard = questionCardRepository.save(card);

        // Save in ElasticSearch
        ContributionQuestionCardDocument doc = new ContributionQuestionCardDocument();
        doc.setId(savedCard.getId().toString());
        doc.setQuestion(savedCard.getQuestion());
        doc.setAnswer(savedCard.getAnswer());
        doc.setStatus(savedCard.getStatus().name());
        doc.setCounselorId(savedCard.getCounselor().getId().toString());
        doc.setCategory(savedCard.getCategory().getName());

        elasticRepository.save(doc);
        return savedCard;
    }

    @Override
    @Transactional
    public ContributionQuestionCard updateContributionQuestionCard(Long id, String question, String answer, Long categoryId, ContributionQuestionCard.Status status, Long counselorId) {
        Counselor counselor = getCounselor(counselorId);

        ContributedQuestionCardCategory category = categoryRepository.findById(categoryId).orElseThrow();
        // Update in MySQL
        ContributionQuestionCard card = questionCardRepository.findById(id).orElseThrow();
        card.setQuestion(question);
        card.setAnswer(answer);
        card.setCategory(category);
        card.setStatus(status);
        card.setCounselor(counselor);

        ContributionQuestionCard updatedCard = questionCardRepository.save(card);

        // Update in ElasticSearch
        ContributionQuestionCardDocument doc = new ContributionQuestionCardDocument();
        doc.setId(updatedCard.getId().toString());
        doc.setQuestion(updatedCard.getQuestion());
        doc.setAnswer(updatedCard.getAnswer());
        doc.setStatus(updatedCard.getStatus().name());
        doc.setCounselorId(updatedCard.getCounselor().getId().toString());
        doc.setCategory(updatedCard.getCategory().getName());

        elasticRepository.save(doc);
        return updatedCard;
    }

    @Override
    @Transactional
    public void deleteContributionQuestionCard(Long id) {
        questionCardRepository.deleteById(id);
        elasticRepository.deleteById(id.toString());
    }

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public List<ContributionQuestionCardResponseDTO> searchContributionQuestionCards(String query, String status, Long counselorId, Long categoryId) {
        Query boolQuery = Query.of(b -> b
                .bool(bool -> {
                    bool.must(m -> m
                            .match(f -> f
                                    .field("question")
                                    .query(query)
                                    .fuzziness("AUTO")
                            )
                    );

                    if (status != null) {
                        bool.filter(f -> f.term(t -> t.field("status").value(status)));
                    }

                    if (counselorId != null) {
                        bool.filter(f -> f.term(t -> t.field("counselorId").value(counselorId)));
                    }

                    if (categoryId != null) {
                        bool.filter(f -> f.term(t -> t.field("categoryId").value(categoryId)));
                    }

                    return bool;
                })
        );

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("contribution_question_cards")
                .query(boolQuery)
        );

        SearchResponse<ContributionQuestionCardDocument> response;
        try {
            response = elasticsearchClient.search(searchRequest, ContributionQuestionCardDocument.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to search contribution question cards", e);
        }

        List<ContributionQuestionCardDocument> results = new ArrayList<>();
        for (Hit<ContributionQuestionCardDocument> hit : response.hits().hits()) {
            results.add(hit.source());
        }

        return results.stream()
                .map(ContributionQuestionCardMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}

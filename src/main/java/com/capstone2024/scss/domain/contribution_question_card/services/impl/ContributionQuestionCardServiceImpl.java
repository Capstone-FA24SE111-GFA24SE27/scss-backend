package com.capstone2024.scss.domain.contribution_question_card.services.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.contribution_question_card.dto.ContributionQuestionCardResponseDTO;
import com.capstone2024.scss.domain.common.mapper.contribution_question_card.ContributionQuestionCardMapper;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributedQuestionCardCategory;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributionQuestionCard;
import com.capstone2024.scss.domain.contribution_question_card.services.ContributionQuestionCardService;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.infrastructure.configuration.openai.OpenAIService;
import com.capstone2024.scss.infrastructure.elastic_search.documents.ContributionQuestionCardDocument;
import com.capstone2024.scss.infrastructure.elastic_search.repository.ContributionQuestionCardElasticRepository;
import com.capstone2024.scss.infrastructure.repositories.contribution_question_card.ContributedQuestionCardCategoryRepository;
import com.capstone2024.scss.infrastructure.repositories.contribution_question_card.ContributionQuestionCardRepository;
import com.capstone2024.scss.infrastructure.repositories.counselor.CounselorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContributionQuestionCardServiceImpl implements ContributionQuestionCardService {

    private final ContributionQuestionCardRepository questionCardRepository;
    private final ContributionQuestionCardElasticRepository elasticRepository;
    private final ContributedQuestionCardCategoryRepository categoryRepository;
    private final CounselorRepository counselorRepository;
    private final OpenAIService openAIService;

    private Counselor getCounselor(Long counselorId) {

        return counselorRepository.findById(counselorId)
                .orElseThrow(() -> new NotFoundException("Counselor not found"));
    }


    @Override
    @Transactional
    public ContributionQuestionCard createContributionQuestionCard(String question, String answer, Long categoryId, Long counselorId, String title) {
        Counselor counselor = getCounselor(counselorId);
        ContributedQuestionCardCategory category = categoryRepository.findById(categoryId).orElseThrow();

        // Lấy vector embedding cho question và title từ OpenAI API
        List<Double> questionVectorDouble = openAIService.getEmbeddingFromOpenAPI(question);
        List<Double> titleVectorDouble = openAIService.getEmbeddingFromOpenAPI(title);

        // Chuyển đổi Double -> Float
        List<Float> questionVector = questionVectorDouble.stream()
                .map(Double::floatValue)
                .toList();
        List<Float> titleVector = titleVectorDouble.stream()
                .map(Double::floatValue)
                .toList();

        // Save in MySQL
        ContributionQuestionCard card = new ContributionQuestionCard();
        card.setQuestion(question);
        card.setAnswer(answer);
        card.setPublicStatus(ContributionQuestionCard.PublicStatus.VISIBLE);  // Default status
        card.setCounselor(counselor);
        card.setCategory(category);
        card.setTitle(title);

        ContributionQuestionCard savedCard = questionCardRepository.save(card);

        // Save in ElasticSearch
        ContributionQuestionCardDocument doc = new ContributionQuestionCardDocument();
        doc.setId(savedCard.getId().toString());
        doc.setSortingId(savedCard.getId());
        doc.setQuestion(savedCard.getQuestion());
        doc.setAnswer(savedCard.getAnswer());
        doc.setStatus(savedCard.getPublicStatus().name());
        doc.setCounselorId(savedCard.getCounselor().getId().toString());
        doc.setCategoryId(savedCard.getCategory().getId());
        doc.setTitle(title);

        // Thêm vector embeddings vào ElasticSearch
        doc.setQuestionVector(questionVector);
        doc.setTitleVector(titleVector);

        elasticRepository.save(doc);
        return savedCard;
    }

    @Override
    @Transactional
    public ContributionQuestionCard updateContributionQuestionCard(Long id, String question, String answer, Long categoryId, ContributionQuestionCard.PublicStatus status, Long counselorId, String title) {
        Counselor counselor = getCounselor(counselorId);

        ContributedQuestionCardCategory category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Not found"));
        // Update in MySQL
        ContributionQuestionCard card = questionCardRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found"));
        card.setQuestion(question);
        card.setAnswer(answer);
        card.setCategory(category);
        card.setCounselor(counselor);
        card.setTitle(title);

        ContributionQuestionCard updatedCard = questionCardRepository.save(card);

        // Update in ElasticSearch
        ContributionQuestionCardDocument doc = new ContributionQuestionCardDocument();
        doc.setId(updatedCard.getId().toString());
        doc.setSortingId(updatedCard.getId());
        doc.setQuestion(updatedCard.getQuestion());
        doc.setAnswer(updatedCard.getAnswer());
        doc.setCounselorId(updatedCard.getCounselor().getId().toString());
        doc.setCategoryId(updatedCard.getCategory().getId());
        doc.setTitle(title);

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
    @Transactional
    public PaginationDTO<List<ContributionQuestionCardResponseDTO>> searchContributionQuestionCards(String query, String status, Long counselorId, Long categoryId, Pageable pageable, boolean isSuggestion) {

        // Lấy vector embedding từ query
        List<Double> queryVectorDouble = openAIService.getEmbeddingFromOpenAPI(query);
        List<Float> queryVector = queryVectorDouble.stream()
                .map(Double::floatValue)
                .collect(Collectors.toList());

        Query filterQuery = Query.of(b -> b
                .bool(bool -> {
                    // Thêm điều kiện fuzzy search cho cả question và title
//                    if (query != null && !query.isBlank()) {
//                        bool.should(s -> s
//                                .match(f -> f
//                                        .field("question")
//                                        .query(query)
//                                        .fuzziness("1")
//                                )
//                        );
//                        bool.should(s -> s
//                                .match(f -> f
//                                        .field("title")
//                                        .query(query)
//                                        .fuzziness("1")
//                                )
//                        );
//
//                        // Đảm bảo ít nhất một điều kiện trong should phải thỏa mãn
//                        bool.minimumShouldMatch("1");
//                    } else {
//                        // Nếu query rỗng, sử dụng match_all
//                        bool.must(m -> m
//                                .matchAll(ma -> ma)
//                        );
//                    }

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

        System.out.println(filterQuery.toString());

        // Chuyển đổi queryVector sang JsonData
        JsonData queryVectorJson = JsonData.of(queryVector);

        // Tạo script_score để tính điểm tương tự dựa trên vector
        Query vectorQuery = null;

        if(query != null && query.isEmpty()) {
            vectorQuery = filterQuery;
        } else {
            vectorQuery =  Query.of(q -> q
                    .scriptScore(ss -> ss
                            .query(filterQuery) // Áp dụng các filter
                            .script(script -> script
                                    .inline(inline -> inline
                                            .source(
//                                                    "cosineSimilarity(params.queryVector, 'questionVector') + " +
                                                            "cosineSimilarity(params.queryVector, 'titleVector')"
                                            )
                                            .params(Map.of(
                                                    "queryVector", queryVectorJson // Dùng JsonData thay vì List<Float>
                                            ))
                                    )
                            )
                    )
            );
        }

        Query finalVectorQuery = vectorQuery;
        double minScoreThreshold = isSuggestion ? 0.85 : 0.75;
        SearchRequest searchRequest = SearchRequest.of(sr -> {
                    sr
                            .index("contribution_question_cards")
                            .query(finalVectorQuery)
                            .size(10000);
            if (query == null || query.isEmpty()) {
                // Thêm sắp xếp nếu query rỗng
                sr.sort(so -> so
                        .field(f -> f
                                .field("sortingId")
                                .order(SortOrder.Desc) // Sắp xếp theo thứ tự tăng dần
                        )
                );
            }

            if (query != null && !query.isEmpty()) {
                // Áp dụng min_score khi query không rỗng
                sr.minScore(minScoreThreshold);
            }
            return sr;
                }
        );

        SearchResponse<ContributionQuestionCardDocument> response;
        try {
            response = elasticsearchClient.search(searchRequest, ContributionQuestionCardDocument.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to search contribution question cards", e);
        }

        List<ContributionQuestionCardDocument> elasticResultsRaw = new ArrayList<>();
        for (Hit<ContributionQuestionCardDocument> hit : response.hits().hits()) {
            elasticResultsRaw.add(hit.source());
        }

//        elasticResultsRaw = getPage(elasticResultsRaw, pageable.getPageNumber(), pageable.getPageSize());

        Page<ContributionQuestionCard> contributionQuestionCards = questionCardRepository.findAll(PageRequest.of(0, 9999999));

        List<ContributionQuestionCard> joinValues = getJoinList(contributionQuestionCards.getContent(), elasticResultsRaw);

        List<ContributionQuestionCard> joinValuesPagination = getPage(joinValues, pageable.getPageNumber(), pageable.getPageSize());

        List<ContributionQuestionCardResponseDTO> returnValues = joinValuesPagination.stream()
                .map(ContributionQuestionCardMapper::toResponseDTO)
                .toList();

        return PaginationDTO.<List<ContributionQuestionCardResponseDTO>>builder()
                .data(returnValues)
                .totalPages((joinValues.size() + pageable.getPageSize() - 1) / pageable.getPageSize())
                .totalElements(joinValues.size())
                .build();
    }

    @Override
    public ContributionQuestionCardResponseDTO getOne(Long id) {
        ContributionQuestionCard card = questionCardRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found"));
        return ContributionQuestionCardMapper.toResponseDTO(card);
    }

    private List<ContributionQuestionCard> getJoinList(List<ContributionQuestionCard> contributionQuestionCards, List<ContributionQuestionCardDocument> documents) {
        // Kiểm tra null hoặc danh sách rỗng
        if (contributionQuestionCards == null || documents == null || documents.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> documentIdList = documents.stream().map(document -> Long.valueOf(document.getId())).toList();

        List<ContributionQuestionCard> returnValues = new ArrayList<>();

        for(Long documentId : documentIdList) {
            for(ContributionQuestionCard contributionQuestionCard : contributionQuestionCards) {
                if(contributionQuestionCard.getId().equals(documentId)) {
                    returnValues.add(contributionQuestionCard);
                }
            }
        }

        // Lọc danh sách Student dựa trên studentCode có trong joinedStudentCode
        return returnValues;
    }

    private List<ContributionQuestionCard> getPage(List<ContributionQuestionCard> list, int page, int size) {
        // Kiểm tra danh sách null hoặc trống
        if (list == null || list.isEmpty() || size <= 0 || page < 0) {
            return Collections.emptyList();
        }

        // Tính toán chỉ số bắt đầu và kết thúc
        int start = page * size;
        int end = Math.min(start + size, list.size());

        // Nếu chỉ số bắt đầu vượt quá kích thước danh sách, trả về danh sách rỗng
        if (start >= list.size()) {
            return Collections.emptyList();
        }

        // Trả về danh sách con
        return list.subList(start, end);
    }
}

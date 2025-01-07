package com.capstone2024.scss.domain.q_and_a.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.capstone2024.scss.application.advice.exeptions.ForbiddenException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.common.dto.PaginationDTO;
import com.capstone2024.scss.application.q_and_a.dto.QuestionCardFilterRequestDTO;
import com.capstone2024.scss.application.q_and_a.dto.QuestionCardResponseDTO;
import com.capstone2024.scss.domain.common.helpers.NotificationHelper;
import com.capstone2024.scss.domain.common.mapper.q_and_a.QuestionCardMapper;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionCard;
import com.capstone2024.scss.domain.q_and_a.service.ManageQuestionCardService;
import com.capstone2024.scss.infrastructure.configuration.openai.OpenAIService;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.RabbitMQConfig;
import com.capstone2024.scss.infrastructure.configuration.rabbitmq.dto.RealTimeQuestionDTO;
import com.capstone2024.scss.infrastructure.elastic_search.documents.QuestionCardDocument;
import com.capstone2024.scss.infrastructure.repositories._and_a.QuestionCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManageQuestionCardServiceImpl implements ManageQuestionCardService {

    private final QuestionCardRepository questionCardRepository;
    private final ElasticsearchClient elasticsearchClient;
    private final OpenAIService openAIService;

    @Override
    public PaginationDTO<List<QuestionCardResponseDTO>> getPublicQuestionCardsForManage(QuestionCardFilterRequestDTO filterRequest) {
        log.info("Fetching Question Cards with filters: {}", filterRequest);

//        String query = filterRequest.getKeyword() == null ? "" : filterRequest.getKeyword();
//
//        // Lấy vector embedding từ query
//        List<Double> queryVectorDouble = openAIService.getEmbeddingFromOpenAPI(query);
//        List<Float> queryVector = queryVectorDouble.stream()
//                .map(Double::floatValue)
//                .collect(Collectors.toList());
//
//        Query filterQuery = Query.of(b -> b
//                .bool(bool -> {
////                    if (filterRequest.getType() != null) {
////                        bool.filter(f -> f.term(t -> t.field("questionType").value(filterRequest.getType().name())));
////                    }
//
//                    return bool;
//                })
//        );
//
//        System.out.println(filterQuery.toString());
//
//        // Chuyển đổi queryVector sang JsonData
//        JsonData queryVectorJson = JsonData.of(queryVector);
//
//        // Tạo script_score để tính điểm tương tự dựa trên vector
//        Query vectorQuery = null;
//
//        if(query != null && query.isEmpty()) {
//            vectorQuery = filterQuery;
//        } else {
//            vectorQuery =  Query.of(q -> q
//                    .scriptScore(ss -> ss
//                            .query(filterQuery) // Áp dụng các filter
//                            .script(script -> script
//                                    .inline(inline -> inline
//                                            .source(
//                                                    "cosineSimilarity(params.queryVector, 'contentVector') + " +
//                                                            "cosineSimilarity(params.queryVector, 'titleVector')"
//                                            )
//                                            .params(Map.of(
//                                                    "queryVector", queryVectorJson // Dùng JsonData thay vì List<Float>
//                                            ))
//                                    )
//                            )
//                    )
//            );
//        }
//
//        Query finalVectorQuery = vectorQuery;
//        SearchRequest searchRequest = SearchRequest.of(sr -> sr
//                .index("question_cards")
//                .query(finalVectorQuery)
//                .size(10000)
//        );
//
//        SearchResponse<QuestionCardDocument> response;
//        try {
//            response = elasticsearchClient.search(searchRequest, QuestionCardDocument.class);
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to search contribution question cards", e);
//        }
//
//        List<QuestionCardDocument> elasticResultsRaw = new ArrayList<>();
//        for (Hit<QuestionCardDocument> hit : response.hits().hits()) {
//            elasticResultsRaw.add(hit.source());
//        }

//        elasticResultsRaw = getPage(elasticResultsRaw, filterRequest.getPagination().getPageNumber(), filterRequest.getPagination().getPageSize());

        Page<QuestionCard> questionCardsPage = new PageImpl<>(Collections.emptyList(), filterRequest.getPagination(), 0);

        questionCardsPage = questionCardRepository.findPublicQuestionCardsWithFilterForStudent(
                filterRequest.getKeyword(),
                filterRequest.getStatus(),
                filterRequest.getPublicStatus(),
                filterRequest.getIsClosed(),
                filterRequest.getType(),
                PageRequest.of(0, 999999)
        );

//        List<QuestionCard> joinValues = getJoinList(questionCardsPage.getContent(), elasticResultsRaw);
        List<QuestionCard> joinValues = questionCardsPage.getContent();

        List<QuestionCard> joinValuesPagination = getPage(joinValues, filterRequest.getPagination().getPageNumber(), filterRequest.getPagination().getPageSize());

        List<QuestionCardResponseDTO> questionCardDTOs = joinValuesPagination.stream()
                .map(QuestionCardMapper::toQuestionCardResponseDto)
                .collect(Collectors.toList());

        return PaginationDTO.<List<QuestionCardResponseDTO>>builder()
                .data(questionCardDTOs)
                .totalPages((joinValues.size() + filterRequest.getPagination().getPageSize() - 1) / filterRequest.getPagination().getPageSize())
                .totalElements(joinValues.size())
                .build();
    }

    @Override
    public void updatePublicStatusQuestionCard(Long questionCardId, QuestionCard.PublicStatus questionCardPublicStatus) {
        QuestionCard questionCard = questionCardRepository.findById(questionCardId)
                .orElseThrow(() -> new NotFoundException("Question card not found"));

        if(questionCard.getPublicStatus().equals(QuestionCard.PublicStatus.PENDING)) {
            throw new ForbiddenException("This QC is pending");
        }

        questionCard.setPublicStatus(questionCardPublicStatus);
        questionCardRepository.save(questionCard);
    }

    private List<QuestionCard> getJoinList(List<QuestionCard> questionCards, List<QuestionCardDocument> documents) {
        // Kiểm tra null hoặc danh sách rỗng
        if (questionCards == null || documents == null || documents.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> documentIdList = documents.stream().map(document -> Long.valueOf(document.getId())).toList();

        List<QuestionCard> returnValues = new ArrayList<>();

        for(Long documentId : documentIdList) {
            for(QuestionCard questionCard : questionCards) {
                if(questionCard.getId().equals(documentId)) {
                    returnValues.add(questionCard);
                }
            }
        }

        // Lọc danh sách Student dựa trên studentCode có trong joinedStudentCode
        return returnValues;
    }

    private List<QuestionCard> getPage(List<QuestionCard> list, int page, int size) {
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

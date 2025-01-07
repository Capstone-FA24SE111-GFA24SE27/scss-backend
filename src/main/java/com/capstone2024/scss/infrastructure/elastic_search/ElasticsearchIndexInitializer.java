package com.capstone2024.scss.infrastructure.elastic_search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.json.JsonData;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.io.IOException;

@Configuration
public class ElasticsearchIndexInitializer {

    private final ElasticsearchClient elasticsearchClient;

    public ElasticsearchIndexInitializer(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

//    @PostConstruct
//    public void createIndexIfNotExists() throws IOException {
//        String indexName = "contribution_question_cards";
//
//        // Kiểm tra nếu chỉ mục đã tồn tại
//        boolean indexExists = elasticsearchClient.indices()
//                .exists(ExistsRequest.of(e -> e.index(indexName)))
//                .value();
//
//        if (!indexExists) {
//            // Tạo chỉ mục với mapping
//            CreateIndexResponse createIndexResponse = elasticsearchClient.indices()
//                    .create(CreateIndexRequest.of(i -> i
//                            .index(indexName)
//                            .mappings(m -> m
//                                    .properties("question", p -> p.text(t -> t))
//                                    .properties("title", p -> p.text(t -> t))
//                                    .properties("answer", p -> p.text(t -> t))
//                                    .properties("status", p -> p.keyword(k -> k))
//                                    .properties("counselorId", p -> p.keyword(k -> k))
//                                    .properties("categoryId", p -> p.long_(l -> l))
//                                    .properties("questionVector", p -> p.denseVector(v -> v.dims(1536)))
//                                    .properties("titleVector", p -> p.denseVector(v -> v.dims(1536)))
//                            )
//                    ));
//
//            if (createIndexResponse.acknowledged()) {
//                System.out.println("Index created successfully!");
//            } else {
//                System.out.println("Failed to create index.");
//            }
//        } else {
//            System.out.println("Index already exists.");
//        }
//    }

    @EventListener(ContextClosedEvent.class)
    public void deleteAllIndicesOnShutdown() throws IOException {
        // Lấy danh sách tất cả các chỉ mục hiện có
        var indices = elasticsearchClient.indices().get(GetIndexRequest.of(g -> g.index("*")));
        for (String indexName : indices.result().keySet()) {
            // Xóa từng chỉ mục
            elasticsearchClient.indices().delete(DeleteIndexRequest.of(d -> d.index(indexName)));
            System.out.println("Deleted index: " + indexName);
        }
    }
}

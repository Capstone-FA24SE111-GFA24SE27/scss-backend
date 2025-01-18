package com.capstone2024.scss.domain.student.services.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIResponse {

    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

    @JsonProperty("system_fingerprint")
    private String systemFingerprint;

    @Data
    public static class Choice {
        private int index;
        private Message message;
        private Object logprobs;
        @JsonProperty("finish_reason")
        private String finishReason;

        @Data
        public static class Message {
            private String role;
            private String content;
            private Object refusal;
        }
    }

    @Data
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private int promptTokens;

        @JsonProperty("completion_tokens")
        private int completionTokens;

        @JsonProperty("total_tokens")
        private int totalTokens;

        @JsonProperty("prompt_tokens_details")
        private PromptTokensDetails promptTokensDetails;

        @JsonProperty("completion_tokens_details")
        private CompletionTokensDetails completionTokensDetails;

        @Data
        public static class PromptTokensDetails {
            @JsonProperty("cached_tokens")
            private int cachedTokens;
            @JsonProperty("audio_tokens")
            private int audioTokens;
        }

        @Data
        public static class CompletionTokensDetails {
            @JsonProperty("reasoning_tokens")
            private int reasoningTokens;
            @JsonProperty("audio_tokens")
            private int audioTokens;
            @JsonProperty("accepted_prediction_tokens")
            private int acceptedPredictionTokens;
            @JsonProperty("rejected_prediction_tokens")
            private int rejectedPredictionTokens;
        }
    }
}


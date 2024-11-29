package com.capstone2024.scss.infrastructure.configuration.openai;

import com.capstone2024.scss.domain.student.services.impl.OpenAIRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIRequestForGeneralAssessment {
    @JsonProperty("model")
    private String model;

    @JsonProperty("messages")
    private List<OpenAIRequestForGeneralAssessment.Message> messages;

    @JsonProperty("temperature")
    private double temperature;

    @JsonProperty("max_tokens")
    private int maxTokens;

    @JsonProperty("top_p")
    private double topP;

    @JsonProperty("frequency_penalty")
    private double frequencyPenalty;

    @JsonProperty("presence_penalty")
    private double presencePenalty;

    @JsonProperty("response_format")
    private OpenAIRequestForGeneralAssessment.ResponseFormat responseFormat;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {

        @JsonProperty("role")
        private String role;

        @JsonProperty("content")
        private List<OpenAIRequestForGeneralAssessment.Message.Content> content;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Content {

            @JsonProperty("type")
            private String type;

            @JsonProperty("text")
            private String text;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseFormat {

        @JsonProperty("type")
        private String type;

        @JsonProperty("json_schema")
        private OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema jsonSchema;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class JsonSchema {

            @JsonProperty("name")
            private String name;

            @JsonProperty("strict")
            private boolean strict;

            @JsonProperty("schema")
            private OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema schema;

            @Getter
            @Setter
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Schema {

                @JsonProperty("type")
                private String type;

                @JsonProperty("required")
                private List<String> required;

                @JsonProperty("properties")
                private OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema.Properties properties;

                @JsonProperty("additionalProperties")
                private boolean additionalProperties;

                @Getter
                @Setter
                @NoArgsConstructor
                @AllArgsConstructor
                public static class Properties {

                    @JsonProperty("assessment")
                    private OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema.Properties.Assessment assessment;

                    @Getter
                    @Setter
                    @NoArgsConstructor
                    @AllArgsConstructor
                    public static class Assessment {

                        @JsonProperty("type")
                        private String type;

                        @JsonProperty("description")
                        private String description;
                    }
                }
            }
        }
    }
}

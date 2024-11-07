package com.capstone2024.scss.domain.student.services.impl;

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
public class OpenAIRequest {

    @JsonProperty("model")
    private String model;

    @JsonProperty("messages")
    private List<Message> messages;

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
    private ResponseFormat responseFormat;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {

        @JsonProperty("role")
        private String role;

        @JsonProperty("content")
        private List<Content> content;

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
        private JsonSchema jsonSchema;

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
            private Schema schema;

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
                private Properties properties;

                @JsonProperty("additionalProperties")
                private boolean additionalProperties;

                @Getter
                @Setter
                @NoArgsConstructor
                @AllArgsConstructor
                public static class Properties {

                    @JsonProperty("result")
                    private Result result;

                    @Getter
                    @Setter
                    @NoArgsConstructor
                    @AllArgsConstructor
                    public static class Result {

                        @JsonProperty("type")
                        private String type;

                        @JsonProperty("description")
                        private String description;

                        @JsonProperty("items")
                        private Items items;

                        @Getter
                        @Setter
                        @NoArgsConstructor
                        @AllArgsConstructor
                        public static class Items {

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
}


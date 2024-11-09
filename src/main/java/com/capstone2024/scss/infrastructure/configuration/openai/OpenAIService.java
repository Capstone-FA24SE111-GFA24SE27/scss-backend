package com.capstone2024.scss.infrastructure.configuration.openai;

import com.capstone2024.scss.domain.student.services.impl.OpenAIRequest;
import com.capstone2024.scss.domain.student.services.impl.OpenAIResponse;
import com.capstone2024.scss.infrastructure.configuration.redis.RedisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Enables SLF4J logging
public class OpenAIService {

    private final RestTemplate restTemplate;
    private final RedisService redisService;
    private final String REDIS_PREFIX = "openai_prompt:";
    private final int CACHE_EXPIRATION_HOURS = 6; // Cache expiration time in hours

    @Value("${openai.api.key}")
    private String openAiApiKey;

    public String callOpenAPI(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            log.warn("Empty prompt received, returning an empty string.");
            return "";
        }

        // Generate a unique Redis key based on the prompt's hash
        String redisKey = REDIS_PREFIX + prompt.hashCode();
        log.info("Redis key: {}", redisKey);

        // Check if the result for this prompt already exists in Redis
        if (redisService.exists(redisKey)) {
            log.info("Cache hit for prompt: {}", prompt);
            return (String) redisService.getData(redisKey);
        }
        log.info("Cache miss for prompt: {}, proceeding to call OpenAI API", prompt);

        // Create the request content, message, and response format objects
        OpenAIRequest.Message.Content content = new OpenAIRequest.Message.Content("text", prompt);
        OpenAIRequest.Message message = new OpenAIRequest.Message("user", List.of(content));
        OpenAIRequest.ResponseFormat.JsonSchema.Schema.Properties.Result.Items resultItems = new OpenAIRequest.ResponseFormat.JsonSchema.Schema.Properties.Result.Items("string", "A string tag in the array.");
        OpenAIRequest.ResponseFormat.JsonSchema.Schema.Properties.Result result = new OpenAIRequest.ResponseFormat.JsonSchema.Schema.Properties.Result(
                "array",
                "An array of strings.",
                resultItems
        );
        OpenAIRequest.ResponseFormat.JsonSchema.Schema.Properties properties = new OpenAIRequest.ResponseFormat.JsonSchema.Schema.Properties(result);
        OpenAIRequest.ResponseFormat.JsonSchema.Schema schema = new OpenAIRequest.ResponseFormat.JsonSchema.Schema(
                "object",
                List.of("result"),
                properties,
                false
        );
        OpenAIRequest.ResponseFormat.JsonSchema jsonSchema = new OpenAIRequest.ResponseFormat.JsonSchema("string_array", true, schema);
        OpenAIRequest.ResponseFormat responseFormat = new OpenAIRequest.ResponseFormat("json_schema", jsonSchema);

        OpenAIRequest request = new OpenAIRequest(
                "gpt-4o",
                List.of(message),
                0.3,
                2048,
                1,
                0,
                0,
                responseFormat
        );

        // Set up HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + openAiApiKey);

        String openAiUrl = "https://api.openai.com/v1/chat/completions";
        HttpEntity<OpenAIRequest> entity = new HttpEntity<>(request, headers);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Log the API call
            log.info("Sending request to OpenAI API for prompt: {}", prompt);
            ResponseEntity<String> response = restTemplate.exchange(openAiUrl, HttpMethod.POST, entity, String.class);

            // Log successful response
            log.info("Received response from OpenAI API for prompt: {}", prompt);

            OpenAIResponse openAIResponse = objectMapper.readValue(response.getBody(), OpenAIResponse.class);
            List<String> contents = openAIResponse.getChoices().stream()
                    .map(choice -> choice.getMessage().getContent())
                    .collect(Collectors.toList());

            JsonNode rootNode = objectMapper.readTree(contents.get(0));
            JsonNode resultNode = rootNode.get("result");

            // Convert the result to a comma-separated string
            String resultString = String.join(", ",
                    objectMapper.convertValue(resultNode, List.class));

            // Store the result in Redis with an expiration time
            redisService.saveDataWithExpiration(redisKey, resultString, CACHE_EXPIRATION_HOURS, TimeUnit.HOURS);

            log.info("Cached OpenAI response in Redis with key: {}", redisKey);
            return resultString;

        } catch (Exception e) {
            log.error("Error parsing OpenAI response for prompt: {}", prompt, e);
            throw new RuntimeException("Error parsing OpenAI response", e);
        }
    }
}

package com.capstone2024.scss.infrastructure.configuration.openai;

import com.capstone2024.scss.domain.counselor.entities.Expertise;
import com.capstone2024.scss.domain.demand.entities.ProblemTag;
import com.capstone2024.scss.domain.student.entities.Student;
import com.capstone2024.scss.domain.student.services.impl.OpenAIRequest;
import com.capstone2024.scss.domain.student.services.impl.OpenAIResponse;
import com.capstone2024.scss.infrastructure.configuration.openai.dto.SubjectOpenAi;
import com.capstone2024.scss.infrastructure.configuration.redis.RedisService;
import com.capstone2024.scss.infrastructure.repositories.counselor.ExpertiseRepository;
import com.capstone2024.scss.infrastructure.repositories.demand.ProblemTagRepository;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Enables SLF4J logging
public class OpenAIService {

    private final RestTemplate restTemplate;
    private final RedisService redisService;
    private final String REDIS_PREFIX_GET_TAG_FROM_COMMENT_PROMPT = "comment_tag:";
    private final String REDIS_PREFIX_ASSESSMENT_PROMPT = "assessment:";
    private final String REDIS_PREFIX_REASON_MEANING_PROMPT = "reason_meaning:";
    private final String REDIS_PREFIX_EXPERTISE_PROMPT = "expertise:";
    private final int CACHE_EXPIRATION_HOURS = 6; // Cache expiration time in hours
    private final ProblemTagRepository problemTagRepository;
    private final ExpertiseRepository expertiseRepository;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    public String generatePromptToOpenAIForParseBehaviorTag(String prompt) {
        List<String> predefinedTags = problemTagRepository.findAll()
                .stream()
                .map(ProblemTag::getName)
                .collect(Collectors.toList());

        String preDefinedTags = String.join(", ", predefinedTags);

        String promptCommand = "Pre-defined tags: [" + preDefinedTags + "]\n" +
                "Prompt: ["+ prompt + "]\n" +
                "Your action: [\n" +
                "Content Analysis: Before tagging, analyze the prompt to identify its meaning, sentiment, and specific behaviors related to the student.\n" +
                "Filter Out Stop Words: Remove unnecessary stop words to simplify the sentence and focus on key terms related to behavior and academic performance.\n" +
                "Match with Available Tags: Based on the filtered keywords, compare them with the available tags to find the most relevant ones.\n" +
                "Assign Multiple Tags if Necessary: In cases where multiple aspects of behavior are described, multiple tags may be assigned, with each tag representing a specific negative behavior or area of weakness.\n" +
                "If no tags match: If the prompt does not clearly match any of the pre-defined tags, return an empty response or a default tag indicating no relevant tags found.\n" +
                "]\n" +
                "Response: [JSON format with key (result) and value (String array of tags)]";

//        System.out.println(promptCommand);
        return promptCommand;
    }

    public String generatePromptToOpenAIForDefineReasonMeaning(String reason, Student student) {
        if(student == null || reason == null || reason.isEmpty()) {
            return "NOT_DETAIL_ENOUGH";
        }

        String promptCommand = "You are a student counseling system. Below is the information about a student:\n" +
                "- Department: " + (student.getDepartment() != null ? student.getDepartment().getName() : "N/A") + "\n" +
                "- Major: " + (student.getMajor() != null ? student.getMajor().getName() : "N/A") + "\n" +
                "- Reason for Counseling: " + reason + "\n\n" +
                "Please determine whether the student’s reason for counseling is related to academic or non-academic issues. " +
                "The response should be one of the following values:\n" +
                "1. ACADEMIC: If the reason is related to academic issues, research, or academic activities.\n" +
                "2. NON_ACADEMIC: If the reason is related to non-academic issues (e.g., mental health, social issues, career).\n" +
                "3. NOT_DETAIL_ENOUGH: If the provided information is not sufficient to determine the counseling reason.\n" +
                "4. ACADEMIC_BUT_OUT_OF_YOUR_ACADEMIC_SCOPE: If the reason is academic but outside the scope of your department’s academic scope.\n\n" +
                "Based on the provided information, please return the appropriate result.\n" +
                "The result is always one of the following four values (ACADEMIC, NON_ACADEMIC, NOT_DETAIL_ENOUGH, ACADEMIC_BUT_OUT_OF_YOUR_ACADEMIC_SCOPE).\n";

        return promptCommand;
    }

    public String generatePromptToOpenAIForBestExpertiseMatching(String reason) {
        List<Expertise> expertiseList = expertiseRepository.findAll();
        if (reason == null || reason.isEmpty() || expertiseList.isEmpty()) {
            return null;
        }

        // Build the list of expertise options from the database
        StringBuilder expertiseOptions = new StringBuilder();
        for (Expertise expertise : expertiseList) {
            expertiseOptions.append("- ").append(expertise.getName()).append("\n");
        }

        // Format the prompt
        String promptCommand = String.format(
                "You are a system that helps determine the most suitable expertise based on the student's counseling request. Below is a list of available expertise areas:\n\n%s" +
                        "Student Reason for Counseling:\n%s\n\nPlease analyze the student's counseling reason and determine which expertise area would be most suitable for addressing the student's needs. " +
                        "Please return the expertise that matches best. The result should only be one of the above available expertise, without any redundant words.",
                expertiseOptions.toString(), reason
        );

        return promptCommand;
    }

    public String generatePromptForGeneralAssessment(List<SubjectOpenAi> subjects) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Provide an overview of a student’s academic and behavioral performance based on the following data.\n\n");
        prompt.append("If Subject does not have any Behavior Tags, response with nothing to assess.\n\n");
        prompt.append("For each course, I will provide:\n");
//        prompt.append("- Subject name\n");
//        prompt.append("- Grade (or '-' if not available yet)\n");
//        prompt.append("- Number of slots (total classes)\n");
//        prompt.append("- Number of slots present\n");
//        prompt.append("- Number of slots absent\n");
//        prompt.append("- Number of remaining slots\n\n");
        prompt.append("For behavior, I will list tags with the frequency of each tag.\n\n");
        prompt.append("Data:\n");

        // Loop through each subject and format the information
        for (SubjectOpenAi subject : subjects) {
            prompt.append("- **Subject:** ").append(subject.getName()).append("\n");
//            prompt.append("  - **Grade:** ").append(subject.getGrade()).append("\n");
//            prompt.append("  - **Total Slots:** ").append(subject.getTotalSlots()).append("\n");
//            prompt.append("  - **Slots Present:** ").append(subject.getSlotsPresent()).append("\n");
//            prompt.append("  - **Slots Absent:** ").append(subject.getSlotsAbsent()).append("\n");
//            prompt.append("  - **Remaining Slots:** ").append(subject.getRemainingSlots()).append("\n");
            prompt.append("  - **Behavior Tags:** ");
            if (subject.getBehaviorTags().isEmpty()) {
                prompt.append("None\n");
            } else {
                for (Map.Entry<String, Integer> tag : subject.getBehaviorTags().entrySet()) {
                    prompt.append("\"").append(tag.getKey()).append("\" (").append(tag.getValue()).append("), ");
                }
                // Remove the trailing comma and space
                prompt.setLength(prompt.length() - 2);
                prompt.append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("Provide a concise, high-level assessment in bullet points including Behavioral Performance Overview and Overall Assessment for each subject, finally with final conclusion, no need to rewrite behavior tag.");
        prompt.append("Result is markdown form");

        return prompt.toString();
    }

    public String callOpenAPIForParseBehaviorTag(String prompt) {
        if (prompt == null) {
            log.warn("Empty prompt received, returning an empty string.");
            return null;
        }

        if (prompt.isBlank()) {
            log.warn("Empty prompt received, returning an empty string.");
            return null;
        }

        // Generate a unique Redis key based on the prompt's hash
        String redisKey = REDIS_PREFIX_GET_TAG_FROM_COMMENT_PROMPT + prompt.hashCode();
        log.info("Redis key: {}", redisKey);

        // Check if the result for this prompt already exists in Redis
        if (redisService.exists(redisKey)) {
            log.info("Cache hit for prompt: {}", prompt);

            log.info("Cache result: {}", (String) redisService.getData(redisKey));
            String redisValue = (String) redisService.getData(redisKey);
            return redisValue.isEmpty() ? null : redisValue;
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
                "gpt-4o-mini",
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

            List openAITagResponse = objectMapper.convertValue(resultNode, List.class);

            String resultString = "";

            if (!openAITagResponse.isEmpty()) {
                // Convert the result to a comma-separated string
                resultString = String.join(", ", openAITagResponse);
            }

            // Store the result in Redis with an expiration time
            redisService.saveDataWithExpiration(redisKey, resultString, CACHE_EXPIRATION_HOURS, TimeUnit.HOURS);

            log.info("Cached OpenAI response in Redis with key: {}", redisKey);
            return resultString.isEmpty() ? null : resultString;

        } catch (Exception e) {
            log.error("Error parsing OpenAI response for prompt: {}", prompt, e);
            throw new RuntimeException("Error parsing OpenAI response", e);
        }
    }

    public String callOpenAPIForGeneralAssessment(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            log.warn("Empty prompt received, returning an empty string.");
            return "";
        }

        // Generate a unique Redis key based on the prompt's hash
        String redisKey = REDIS_PREFIX_ASSESSMENT_PROMPT + prompt.hashCode();
        log.info("Redis key: {}", redisKey);

        // Check if the result for this prompt already exists in Redis
        if (redisService.exists(redisKey)) {
            log.info("Cache hit for prompt: {}", prompt);

            log.info("Cache result: {}", (String) redisService.getData(redisKey));
            return (String) redisService.getData(redisKey);
        }
        log.info("Cache miss for prompt: {}, proceeding to call OpenAI API", prompt);

        // Create the request content, message, and response format objects
        OpenAIRequestForGeneralAssessment.Message.Content content = new OpenAIRequestForGeneralAssessment.Message.Content("text", prompt);
        OpenAIRequestForGeneralAssessment.Message message = new OpenAIRequestForGeneralAssessment.Message("user", List.of(content));
        OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema.Properties.Assessment assessment = new OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema.Properties.Assessment(
                "string",
                "An text for general assessment."
        );
        OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema.Properties properties = new OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema.Properties(assessment);
        OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema schema = new OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema(
                "object",
                List.of("assessment"),
                properties,
                false
        );
        OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema jsonSchema = new OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema("object", true, schema);
        OpenAIRequestForGeneralAssessment.ResponseFormat responseFormat = new OpenAIRequestForGeneralAssessment.ResponseFormat("json_schema", jsonSchema);

        OpenAIRequestForGeneralAssessment request = new OpenAIRequestForGeneralAssessment(
                "gpt-4o-mini",
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
        HttpEntity<OpenAIRequestForGeneralAssessment> entity = new HttpEntity<>(request, headers);
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
            JsonNode resultNode = rootNode.get("assessment");

            // Convert the result to a comma-separated string
            String resultString = objectMapper.convertValue(resultNode, String.class);

            // Store the result in Redis with an expiration time
            redisService.saveDataWithExpiration(redisKey, resultString, CACHE_EXPIRATION_HOURS, TimeUnit.HOURS);

            log.info("Cached OpenAI response in Redis with key: {}", redisKey);
            return resultString;

        } catch (Exception e) {
            log.error("Error parsing OpenAI response for prompt: {}", prompt, e);
            throw new RuntimeException("Error parsing OpenAI response", e);
        }
    }

    public String callOpenAPIForDefineReasonMeaning(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            log.warn("Empty prompt received, returning an empty string.");
            return "";
        }

        // Generate a unique Redis key based on the prompt's hash
        String redisKey = REDIS_PREFIX_REASON_MEANING_PROMPT + prompt.hashCode();
        log.info("Redis key: {}", redisKey);

        // Check if the result for this prompt already exists in Redis
        if (redisService.exists(redisKey)) {
            log.info("Cache hit for prompt: {}", prompt);

            log.info("Cache result: {}", (String) redisService.getData(redisKey));
            return (String) redisService.getData(redisKey);
        }
        log.info("Cache miss for prompt: {}, proceeding to call OpenAI API", prompt);

        // Create the request content, message, and response format objects
        OpenAIRequestForGeneralAssessment.Message.Content content = new OpenAIRequestForGeneralAssessment.Message.Content("text", prompt);
        OpenAIRequestForGeneralAssessment.Message message = new OpenAIRequestForGeneralAssessment.Message("user", List.of(content));
        OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema.Properties.Assessment assessment = new OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema.Properties.Assessment(
                "string",
                "An text for define meaning of reason."
        );
        OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema.Properties properties = new OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema.Properties(assessment);
        OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema schema = new OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema(
                "object",
                List.of("assessment"),
                properties,
                false
        );
        OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema jsonSchema = new OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema("object", true, schema);
        OpenAIRequestForGeneralAssessment.ResponseFormat responseFormat = new OpenAIRequestForGeneralAssessment.ResponseFormat("json_schema", jsonSchema);

        OpenAIRequestForGeneralAssessment request = new OpenAIRequestForGeneralAssessment(
                "gpt-4o-mini",
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
        HttpEntity<OpenAIRequestForGeneralAssessment> entity = new HttpEntity<>(request, headers);
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
            JsonNode resultNode = rootNode.get("assessment");

            // Convert the result to a comma-separated string
            String resultString = objectMapper.convertValue(resultNode, String.class);

            // Store the result in Redis with an expiration time
            redisService.saveDataWithExpiration(redisKey, resultString, CACHE_EXPIRATION_HOURS, TimeUnit.HOURS);

            log.info("Cached OpenAI response in Redis with key: {}", redisKey);
            return resultString;

        } catch (Exception e) {
            log.error("Error parsing OpenAI response for prompt: {}", prompt, e);
            throw new RuntimeException("Error parsing OpenAI response", e);
        }
    }

    public String callOpenAPIForBestExpertiseMatching(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            log.warn("Empty prompt received, returning an empty string.");
            return "";
        }

        // Generate a unique Redis key based on the prompt's hash
        String redisKey = REDIS_PREFIX_EXPERTISE_PROMPT + prompt.hashCode();
        log.info("Redis key: {}", redisKey);

        // Check if the result for this prompt already exists in Redis
        if (redisService.exists(redisKey)) {
            log.info("Cache hit for prompt: {}", prompt);

            log.info("Cache result: {}", (String) redisService.getData(redisKey));
            return (String) redisService.getData(redisKey);
        }
        log.info("Cache miss for prompt: {}, proceeding to call OpenAI API", prompt);

        // Create the request content, message, and response format objects
        OpenAIRequestForGeneralAssessment.Message.Content content = new OpenAIRequestForGeneralAssessment.Message.Content("text", prompt);
        OpenAIRequestForGeneralAssessment.Message message = new OpenAIRequestForGeneralAssessment.Message("user", List.of(content));
        OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema.Properties.Assessment assessment = new OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema.Properties.Assessment(
                "string",
                "An text for finding best expertise match with student's reason."
        );
        OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema.Properties properties = new OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema.Properties(assessment);
        OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema schema = new OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema.Schema(
                "object",
                List.of("assessment"),
                properties,
                false
        );
        OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema jsonSchema = new OpenAIRequestForGeneralAssessment.ResponseFormat.JsonSchema("object", true, schema);
        OpenAIRequestForGeneralAssessment.ResponseFormat responseFormat = new OpenAIRequestForGeneralAssessment.ResponseFormat("json_schema", jsonSchema);

        OpenAIRequestForGeneralAssessment request = new OpenAIRequestForGeneralAssessment(
                "gpt-4o-mini",
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
        HttpEntity<OpenAIRequestForGeneralAssessment> entity = new HttpEntity<>(request, headers);
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
            JsonNode resultNode = rootNode.get("assessment");

            // Convert the result to a comma-separated string
            String resultString = objectMapper.convertValue(resultNode, String.class);

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

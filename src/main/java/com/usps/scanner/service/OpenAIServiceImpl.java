package com.usps.scanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usps.scanner.model.InspectionResponse;
import com.usps.scanner.model.Issue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Map;

// @Service tells Spring "this class holds business logic - track it as a bean."
// Spring will auto-create one shared instance and inject it wherever needed.
// Concrete implementation of OpenAIService.
// @Service tells Spring to auto-create + inject this where the OpenAIService interface is needed.
@Service
public class OpenAIServiceImpl implements OpenAIService {

    // @Value pulls a config value from application.properties.
    // The :  (with empty default) means "if not set, leave it blank instead of crashing".
    @Value("${openai.api.key:}")
    private String apiKey;

    // Configurable model. Defaults to gpt-4o-mini (~10x cheaper than gpt-4o, still great at vision).
    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    // RestTemplate = Spring's built-in HTTP client. Like fetch() in JavaScript.
    private final RestTemplate restTemplate = new RestTemplate();

    // ObjectMapper = Jackson's JSON converter. Reads/writes JSON strings.
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    // The same prompt from your React Native openai.js - copied character-for-character.
    // Java text blocks (""") let us write multi-line strings cleanly.
    private static final String PROMPT = """
            You are a USPS vehicle inspection AI for LLV mail trucks. Analyze this vehicle image for:
            - Body damage (dents, scratches, rust, paint damage)
            - Tire condition (wear, pressure, damage, alignment)
            - Fluid leaks on ground
            - Lights and mirrors condition
            - Any safety hazards

            Respond ONLY in this JSON format:
            {
              "status": "PASS" | "ATTENTION" | "UNSAFE",
              "issues": [
                {
                  "issue": "short description",
                  "location": "where on vehicle",
                  "severity": "LOW" | "MEDIUM" | "HIGH"
                }
              ],
              "summary": "one sentence"
            }

            If no issues found, return empty issues array with PASS status.
            """;

    // Main method - takes the uploaded image, returns analysis.
    @Override
    public InspectionResponse analyze(MultipartFile image) {

        // No API key configured? Return mock data (matches React Native fallback behavior).
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("YOUR_OPENAI_API_KEY")) {
            return fallback("No API key configured");
        }

        try {
            // STEP 1: Convert image bytes to base64 string.
            // OpenAI's API takes images as base64-encoded text inside JSON.
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

            // STEP 2: Build the JSON request body.
            // OpenAI Vision wants a "messages" array with mixed text + image content.
            // Map.of(...) creates a small JSON-like object inline.
            Map<String, Object> textPart = Map.of("type", "text", "text", PROMPT);
            Map<String, Object> imagePart = Map.of(
                    "type", "image_url",
                    "image_url", Map.of("url", "data:image/jpeg;base64," + base64Image)
            );
            Map<String, Object> message = Map.of(
                    "role", "user",
                    "content", List.of(textPart, imagePart)
            );
            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", List.of(message),
                    "max_tokens", 500,
                    "temperature", 0    // 0 = deterministic, same image gives same answer
            );

            // STEP 3: Set HTTP headers (auth + content type).
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);  // adds "Authorization: Bearer <apiKey>"

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // STEP 4: Make the actual POST to OpenAI.
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_URL, request, String.class);

            // STEP 5: Dig into the response JSON.
            // OpenAI wraps the answer like: { choices: [ { message: { content: "..." } } ] }
            JsonNode root = objectMapper.readTree(response.getBody());
            String content = root.path("choices").get(0).path("message").path("content").asText();

            // STEP 6: GPT sometimes wraps JSON in markdown code fences (```json ... ```).
            // Strip them so we can parse cleanly. (Same logic as your React Native version.)
            String json = content.trim();
            if (json.startsWith("```json")) {
                json = json.substring(7);
            } else if (json.startsWith("```")) {
                json = json.substring(3);
            }
            if (json.endsWith("```")) {
                json = json.substring(0, json.length() - 3);
            }
            json = json.trim();

            // STEP 7: Convert the JSON string into our InspectionResponse object.
            // Jackson auto-maps fields by name (status -> setStatus, etc.)
            return objectMapper.readValue(json, InspectionResponse.class);

        } catch (Exception e) {
            // ANY failure = return fallback. Logging error so we can see what went wrong.
            System.err.println("OpenAI call failed: " + e.getMessage());
            return fallback("OpenAI call failed: " + e.getMessage());
        }
    }

    // Hardcoded fallback - same data as the FALLBACK_RESPONSE in your React Native openai.js.
    private InspectionResponse fallback(String reason) {
        return new InspectionResponse(
                "ATTENTION",
                List.of(
                        new Issue("Possible low rear-left tire", "Rear left wheel", "MEDIUM"),
                        new Issue("Fluid spot under engine area", "Front center undercarriage", "HIGH")
                ),
                "Fallback - " + reason
        );
    }
}

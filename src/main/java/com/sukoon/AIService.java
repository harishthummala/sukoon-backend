package com.sukoon;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getMoodResponse(String mood, String notes) {
        String prompt = "You are Sukoon, a compassionate AI mental wellness " +
                "companion. A user is feeling " + mood + ". " +
                "They shared: '" + notes + "'. " +
                "Respond with empathy, offer one practical suggestion, " +
                "keep response under 25 words.";

        // OpenAI/Groq request format
        String requestBody = "{"
                + "\"model\": \"llama-3.1-8b-instant\","
                + "\"messages\": [{"
                + "\"role\": \"user\","
                + "\"content\": \"" + prompt.replace("\"", "\\\"") + "\""
                + "}]"
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey); // IMPORTANT

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                request,
                Map.class
        );

        // Extract Groq response
        List<Map> choices = (List<Map>) response.getBody().get("choices");
        Map message = (Map) choices.get(0).get("message");

        return (String) message.get("content");
    }
}
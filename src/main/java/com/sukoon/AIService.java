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

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getMoodResponse(String mood, String notes) {
        String prompt = "You are Sukoon, a compassionate AI mental wellness " +
                "companion. A user is feeling " + mood + ". " +
                "They shared: '" + notes + "'. " +
                "Respond with empathy, offer one practical suggestion, " +
                "keep response under 20 words.";

        // Gemini request format
        String requestBody = "{"
                + "\"contents\": [{"
                + "\"parts\": [{"
                + "\"text\": \"" + prompt.replace("\"", "\\\"") + "\""
                + "}]"
                + "}]"
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // Gemini uses API key as query parameter
        ResponseEntity<Map> response = restTemplate.exchange(
                URI.create(apiUrl + "?key=" + apiKey),
                HttpMethod.POST,
                request,
                Map.class
        );

        // Extract Gemini response
        List<Map> candidates = (List<Map>) response.getBody().get("candidates");
        Map content = (Map) candidates.get(0).get("content");
        List<Map> parts = (List<Map>) content.get("parts");
        return (String) parts.get(0).get("text");
    }
}
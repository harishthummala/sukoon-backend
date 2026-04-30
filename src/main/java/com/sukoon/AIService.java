package com.sukoon;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // Regular chat with memory
    public String getMoodResponse(String message, String mood,
                                  List<ChatMessageEntry> history) {

        // Build conversation history for AI memory
        StringBuilder messages = new StringBuilder("[");

        // System message — tells AI who it is
        messages.append("{\"role\":\"system\",\"content\":\"You are Sukoon, ")
                .append("a compassionate AI mental wellness companion and therapist. ")
                .append("The user is feeling ").append(mood).append(". ")
                .append("Respond with empathy, keep responses under 30 words.\"},");

        // Add previous messages for memory
        for(ChatMessageEntry prev : history) {
            // Add user message
            messages.append("{\"role\":\"user\",\"content\":\"")
                    .append(prev.getMessage().replace("\"", "\\\""))
                    .append("\"},");

            // Add AI response if exists
            if(prev.getAiResponse() != null) {
                messages.append("{\"role\":\"assistant\",\"content\":\"")
                        .append(prev.getAiResponse().replace("\"", "\\\""))
                        .append("\"},");
            }
        }

        // Add current new message
        messages.append("{\"role\":\"user\",\"content\":\"")
                .append(message.replace("\"", "\\\""))
                .append("\"}]");

        String requestBody = "{\"model\":\"llama-3.1-8b-instant\","
                + "\"messages\":" + messages + "}";

        return callGroq(requestBody);
    }

    // Generate chat summary at end
    public String getChatSummary(List<ChatMessageEntry> history, String mood) {
        StringBuilder conversation = new StringBuilder();

        for(ChatMessageEntry msg : history) {
            conversation.append("User: ").append(msg.getMessage()).append("\n");
            if(msg.getAiResponse() != null) {
                conversation.append("Sukoon: ").append(msg.getAiResponse()).append("\n");
            }
        }

        String summaryPrompt = "Summarize this mental wellness conversation "
                + "in 3 sentences. User's mood was: " + mood
                + ". Conversation:\n" + conversation;

        String requestBody = "{\"model\":\"llama-3.1-8b-instant\","
                + "\"messages\":[{\"role\":\"user\",\"content\":\""
                + summaryPrompt.replace("\"", "\\\"")
                + "\"}]}";

        return callGroq(requestBody);
    }

    // Shared Groq API caller
    private String callGroq(String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                request,
                Map.class
        );

        List<Map> choices = (List<Map>) response.getBody().get("choices");
        Map msg = (Map) choices.get(0).get("message");
        return (String) msg.get("content");
    }
}
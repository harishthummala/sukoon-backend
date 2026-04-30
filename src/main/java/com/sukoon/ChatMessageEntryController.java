package com.sukoon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat-message")
public class ChatMessageEntryController {

    @Autowired
    private ChatMessageEntryRepository chatMessageEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private AIService aiService;

    @Autowired
    private MoodEntryRepository moodEntryRepository;


    @PostMapping("/{chatId}/addmessage")
    public Map<String, Object> addMessage(
            @PathVariable Long chatId,
            @RequestBody ChatMessageEntry chatMessageEntry) {

        // Find chat
        Chat chat = chatRepository.findById(chatId).orElse(null);
        if(chat == null) {
            return Map.of("error", "Chat not found");
        }

        // Check if chat already ended
        if(chat.isEnded()) {
            return Map.of(
                    "error", "This chat has ended",
                    "summary", chat.getSummary(),
                    "message", "Start a new chat to continue"
            );
        }

        int count = chatMessageEntryRepository.countByChatId(chatId);

        if (count > 20) {
            chat.setEnded(true);
            chatRepository.save(chat);

            return Map.of(
                    "error", "Chat limit reached (20 messages)",
                    "chatEnded", true
            );
        }

        // Get mood for this chat
        List<MoodEntry> moods = moodEntryRepository.findByChatId(chatId);
        String mood = moods.isEmpty() ? "neutral" : moods.get(0).getMood();

        // Get chat history for AI memory
        List<ChatMessageEntry> history =
                chatMessageEntryRepository.findByChatIdOrderByTimestampAsc(chatId);

        // Get AI response with memory
        String aiResponse;
        try {
            aiResponse = aiService.getMoodResponse(
                    chatMessageEntry.getMessage(),
                    mood,
                    history  // ← sends full history for memory
            );
        } catch(Exception e) {
            aiResponse = "Sorry, something went wrong. Try again.";
        }

        // Save message
        chatMessageEntry.setChat(chat);
        chatMessageEntry.setAiResponse(aiResponse);
        chatMessageEntryRepository.save(chatMessageEntry);

        // Increment message count
        chatMessageEntry.setMessageCount(chatMessageEntry.getMessageCount() + 1);
        chatRepository.save(chat);


        // Auto end chat if limit reached
        if(chatMessageEntry.getMessageCount() > 20) {
            Map<String, Object> endResult = endChat(chat);
            return Map.of(
                    "message", chatMessageEntry,
                    "aiResponse", aiResponse,
                    "chatEnded", true,
                    "summary", endResult.get("summary")
            );
        }

        return Map.of(
                "message", chatMessageEntry,
                "messageCount", count+1,
                "aiResponse", aiResponse,
                "chatEnded", false
        );
    }

    // End chat + generate summary
    private Map<String, Object> endChat(Chat chat) {
        List<ChatMessageEntry> history =
                chatMessageEntryRepository.findByChatIdOrderByTimestampAsc(chat.getId());

        // Get mood
        List<MoodEntry> moods =
                moodEntryRepository.findByChatId(chat.getId());
        String mood = moods.isEmpty() ? "neutral" : moods.get(0).getMood();

        // Generate AI summary
        String summary;
        try {
            summary = aiService.getChatSummary(history, mood);
        } catch(Exception e) {
            summary = "Chat session completed with " +
                    history.size() + " messages.";
        }

        // Save summary and mark chat as ended
        chat.setSummary(summary);
        chat.setEnded(true);
        chatRepository.save(chat);

        return Map.of(
                "chatEnded", true,
                "summary", summary,
                "totalMessages", history.size(),
                "message", "Chat limit reached. Here's your session summary."
        );
    }

    // Manual end chat endpoint
    @PostMapping("/{chatId}/end")
    public Map<String, Object> endChatManually(@PathVariable Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElse(null);
        if(chat == null) {
            return Map.of("error", "Chat not found");
        }
        return endChat(chat);
    }


    @GetMapping("/{chatId}/count")
    public ResponseEntity<Map<String, Object>> getMessageCount(
            @PathVariable Long chatId,
            @RequestHeader("Authorization") String authHeader) {

        // ✅ Extract email from JWT
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);

        // ✅ Find user
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized access"));
        }

        // ✅ Find chat
        Chat chat = chatRepository.findById(chatId).orElse(null);

        if (chat == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Chat not found"));
        }

        // ✅ SECURITY CHECK (VERY IMPORTANT)
        if (!chat.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Unauthorized access"));
        }

        // ✅ Count messages
        int count = chatMessageEntryRepository.countByChatId(chatId);

        return ResponseEntity.ok(Map.of(
                "chatId", chatId,
                "messageCount", count,
                "limit", 20
        ));
    }

    // Get chat history
    @GetMapping("/{chatId}/history")
    public List<ChatMessageEntry> getChatHistory(@PathVariable Long chatId) {
        return chatMessageEntryRepository
                .findByChatIdOrderByTimestampAsc(chatId);
    }

}
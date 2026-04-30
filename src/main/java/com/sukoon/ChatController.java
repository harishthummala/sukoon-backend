package com.sukoon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatMessageEntryRepository messageRepository;

    @Autowired
    private MoodEntryRepository moodEntryRepository;

    @Autowired
    private AIService aiService;

    @Autowired
    private JwtUtil jwtUtil;

    // Create chat session
    @PostMapping
    public ResponseEntity<Map<String, Object>> createChat(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {

        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepository.findByEmail(email);
        String mood = body.get("mood");

        Chat chat = new Chat();
        chat.setUser(user);
        Chat saved = chatRepository.save(chat);

        // Save mood
        MoodEntry moodEntry = new MoodEntry();
        moodEntry.setMood(mood);
        moodEntry.setChat(saved);
        moodEntryRepository.save(moodEntry);

        return ResponseEntity.status(201).body(Map.of(
                "id", saved.getId().toString(),
                "mood", mood,
                "title", mood + " Chat - " + LocalDateTime.now().toLocalDate(),
                "messages", List.of(),
                "createdAt", saved.getTimestamp().toString()
        ));
    }

    // Get all chats for user
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllChats(
            @RequestHeader("Authorization") String authHeader) {

        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepository.findByEmail(email);
        List<Chat> chats = chatRepository
                .findByUserIdOrderByTimestampDesc(user.getId());

        List<Map<String, Object>> result = chats.stream().map(chat -> {
            List<MoodEntry> moods = moodEntryRepository
                    .findByChatId(chat.getId());
            String mood = moods.isEmpty() ? "neutral" : moods.get(0).getMood();

            return Map.<String, Object>of(
                    "id", chat.getId().toString(),
                    "mood", mood,
                    "title", mood + " Chat - " + chat.getTimestamp().toLocalDate(),
                    "createdAt", chat.getTimestamp().toString()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // Get specific chat with messages
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getChat(
            @PathVariable Long id) {

        Chat chat = chatRepository.findById(id).orElse(null);
        if(chat == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Chat session not found"));
        }

        List<MoodEntry> moods = moodEntryRepository.findByChatId(id);
        String mood = moods.isEmpty() ? "neutral" : moods.get(0).getMood();

        List<ChatMessageEntry> history = messageRepository
                .findByChatIdOrderByTimestampAsc(id);

        // Format messages like V0 expects
        List<Map<String, Object>> messages = history.stream()
                .flatMap(msg -> {
                    Map<String, Object> userMsg = Map.of(
                            "id", "u_" + msg.getId(),
                            "sessionId", id.toString(),
                            "content", msg.getMessage(),
                            "sender", "user",
                            "createdAt", msg.getTimestamp().toString()
                    );
                    Map<String, Object> aiMsg = Map.of(
                            "id", "a_" + msg.getId(),
                            "sessionId", id.toString(),
                            "content", msg.getAiResponse() != null ?
                                    msg.getAiResponse() : "",
                            "sender", "assistant",
                            "createdAt", msg.getTimestamp().toString()
                    );
                    return java.util.stream.Stream.of(userMsg, aiMsg);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "id", chat.getId().toString(),
                "mood", mood,
                "title", mood + " Chat - " + chat.getTimestamp().toLocalDate(),
                "messages", messages,
                "createdAt", chat.getTimestamp().toString()
        ));
    }

    // Send message
    @PostMapping("/{id}/message")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        Chat chat = chatRepository.findById(id).orElse(null);
        if(chat == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Chat not found"));
        }

        if(chat.isEnded()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Chat has ended"));
        }


        String message = body.get("message");
        List<MoodEntry> moods = moodEntryRepository.findByChatId(id);
        String mood = moods.isEmpty() ? "neutral" : moods.get(0).getMood();

        List<ChatMessageEntry> history = messageRepository
                .findByChatIdOrderByTimestampAsc(id);

        String aiResponse;
        try {
            aiResponse = aiService.getMoodResponse(message, mood, history);
        } catch(Exception e) {
            aiResponse = "I'm here for you. Please try again.";
        }

        ChatMessageEntry entry = new ChatMessageEntry();
        entry.setMessage(message);
        entry.setAiResponse(aiResponse);
        entry.setChat(chat);
        messageRepository.save(entry);



        return ResponseEntity.status(201).body(Map.of(
                "id", entry.getId().toString(),
                "sessionId", id.toString(),
                "content", message,
                "sender", "user",
                "aiResponse", aiResponse,
                "createdAt", entry.getTimestamp().toString()
        ));
    }

    // End chat
    @PostMapping("/{id}/end")
    public ResponseEntity<Map<String, Object>> endChat(
            @PathVariable Long id) {

        Chat chat = chatRepository.findById(id).orElse(null);
        if(chat == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Chat not found"));
        }

        endChatInternal(chat);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Chat session ended",
                "summary", chat.getSummary() != null ?
                        chat.getSummary() : ""
        ));
    }

    private void endChatInternal(Chat chat) {
        List<ChatMessageEntry> history = messageRepository
                .findByChatIdOrderByTimestampAsc(chat.getId());
        List<MoodEntry> moods = moodEntryRepository
                .findByChatId(chat.getId());
        String mood = moods.isEmpty() ? "neutral" : moods.get(0).getMood();

        try {
            String summary = aiService.getChatSummary(history, mood);
            chat.setSummary(summary);
        } catch(Exception e) {
            chat.setSummary("Session completed.");
        }
        chat.setEnded(true);
        chatRepository.save(chat);
    }

    @DeleteMapping({"/{id}/delete"})
    public ResponseEntity<Map<String, Object>> deleteChat(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized access"));
        }

        Chat chat = chatRepository.findById(id).orElse(null);
        if (chat == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Chat not found"));
        }

        if (!chat.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Unauthorized"));
        }

        chatRepository.delete(chat);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Chat deleted"
        ));
    }
}

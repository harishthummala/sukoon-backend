package com.sukoon;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message_entries")
public class ChatMessageEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String message;      // user's message

    @Column(name = "ai_response", columnDefinition = "TEXT")
    private String aiResponse;   // AI's response

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    // Default constructor
    public ChatMessageEntry() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getMessage() { return message; }
    public String getAiResponse() { return aiResponse; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Chat getChat() { return chat; }

    // Setters
    public void setMessage(String message) { this.message = message; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setChat(Chat chat) { this.chat = chat; }
}
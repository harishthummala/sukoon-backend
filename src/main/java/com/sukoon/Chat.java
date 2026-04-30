package com.sukoon;

import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime timestamp;
    private String summary;         // stores final summary
    private boolean isEnded = false; // tracks if chat is over

    //Many journals belong to One user
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MoodEntry> moodEntries = new ArrayList<>();

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessageEntry> messageEntries = new ArrayList<>();

    //default constructor
    public Chat(){
        this.timestamp = LocalDateTime.now();
    }



    //getters

    public Long getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public User getUser() { return user; }
    public String getSummary() { return summary; }
    public boolean isEnded() { return isEnded; }

    //setters
    public void setUser(User user) { this.user = user; }
    public void setTimestamp( LocalDateTime timestamp){
        this.timestamp = timestamp;
    }
    public void setId(Long id) { this.id = id; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setEnded(boolean ended) { isEnded = ended; }

}

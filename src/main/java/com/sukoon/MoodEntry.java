package com.sukoon;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name = "mood_entries")

public class MoodEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private String mood;
    private String notes;
    private LocalDateTime timestamp;
    @Column(name = "ai_response")
    private String aiResponse;


    //Many mood entries belong to One user
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    //default constructor
    public MoodEntry(){
        this.timestamp = LocalDateTime.now();
    }

    //getters
    public long getId() { return id;}
    public String getMood() { return mood; }
    public String getNotes() {return notes; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public User getUser() { return user; }
    public String getAiResponse() { return aiResponse; }

    //setters
    public void setMood(String mood) { this.mood = mood; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setUser(User user) { this.user = user; }
    public void setTimestamp(LocalDateTime timestamp){
        this.timestamp = timestamp;
    }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }

}

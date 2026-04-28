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
    private LocalDateTime timestamp;


    //Many mood entries belong to One user
    @ManyToOne
    @JoinColumn(name="chat_id")
    private Chat chat;

    //default constructor
    public MoodEntry(){
        this.timestamp = LocalDateTime.now();
    }

    //getters
    public long getId() { return id;}
    public String getMood() { return mood; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Chat getChat() {return chat;}

    //setters
    public void setMood(String mood) { this.mood = mood; }
    public void setChat(Chat chat) { this.chat = chat; }
    public void setTimestamp(LocalDateTime timestamp){
        this.timestamp = timestamp;
    }

}

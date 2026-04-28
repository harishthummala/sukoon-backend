package com.sukoon;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "journals")
public class JournalEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private LocalDateTime timestamp;

    //Many journals belong to One user
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    //default constructor
    public JournalEntry(){
        this.timestamp = LocalDateTime.now();
    }

    //getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public User getUser() { return user; }

    //setters
    public void setUser(User user) { this.user = user; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(LocalDateTime timestamp){
        this.timestamp = timestamp;
    }

}

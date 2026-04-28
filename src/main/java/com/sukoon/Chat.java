package com.sukoon;

import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime timestamp;
    private int messageCount = 0;  // tracks messages sent
    private String summary;         // stores final summary
    private boolean isEnded = false; // tracks if chat is over

    //Many journals belong to One user
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    //default constructor
    public Chat(){
        this.timestamp = LocalDateTime.now();
    }



    //getters

    public Long getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public User getUser() { return user; }
    public int getMessageCount() { return messageCount; }
    public String getSummary() { return summary; }
    public boolean isEnded() { return isEnded; }

    //setters
    public void setUser(User user) { this.user = user; }
    public void setTimestamp( LocalDateTime timestamp){
        this.timestamp = timestamp;
    }
    public void setId(Long id) { this.id = id; }
    public void setMessageCount(int messageCount) { this.messageCount = messageCount; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setEnded(boolean ended) { isEnded = ended; }

}

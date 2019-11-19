package com.example.chat.dto;

import com.example.chat.domain.User;

import java.time.LocalDateTime;

public class MessageDTO {
    private User sender;
    private String text;
    private LocalDateTime timestamp;

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

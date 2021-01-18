package com.example.demo.model;

import lombok.Builder;
import lombok.Data;

@Data
public class ChatMessage {
	private MessageType type;
    private String content;
    private String sender;
    private String group;
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}